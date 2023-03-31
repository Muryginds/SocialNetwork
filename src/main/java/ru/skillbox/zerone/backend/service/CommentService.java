package ru.skillbox.zerone.backend.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.CommentNotFoundException;
import ru.skillbox.zerone.backend.exception.NotificationPermissionException;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.request.CommentRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommentDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.StorageDTO;
import ru.skillbox.zerone.backend.model.entity.Comment;
import ru.skillbox.zerone.backend.model.entity.NotificationSetting;
import ru.skillbox.zerone.backend.model.entity.Post;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.model.enumerated.CommentType;
import ru.skillbox.zerone.backend.repository.CommentRepository;
import ru.skillbox.zerone.backend.repository.NotificationSettingRepository;
import ru.skillbox.zerone.backend.repository.PostRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommentService {
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final UserMapper userMapper;
  private final NotificationService notificationService;
  private final NotificationSettingRepository notificationSettingRepository;
  private final NotificationSettingService notificationSettingService;

  public CommonListResponseDTO<CommentDTO> getPage4Comments(int offset, int itemPerPage, Post post, User user) {

    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
    Page<Comment> pageableCommentList = commentRepository
        .findCommentsByPostIdAndParentNull(post.getId(), pageable);

    return getPostResponse(offset, itemPerPage, pageableCommentList, user);
  }

  public CommonListResponseDTO<CommentDTO> getComments(int offset, int itemPerPage, long id) {
    User user = CurrentUserUtils.getCurrentUser();
    Post post = postRepository.findById(id).orElseThrow();
    return getPage4Comments(offset, itemPerPage, post, user);
  }

  public List<CommentDTO> getCommentDTO4Response(Set<Comment> comments, User user) {
    List<CommentDTO> commentDTOList = new ArrayList<>();
    comments.forEach(comment -> {
      CommentDTO commentData = getCommentDTO(comment, user);
      commentData.getSubComments().add(getCommentDTO(comment, user));
      commentDTOList.add(commentData);
    });
    return new ArrayList<>(commentDTOList);
  }

  public CommonResponseDTO<CommentDTO> addComment(long id, CommentRequestDTO commentRequest) {
    User user = CurrentUserUtils.getCurrentUser();
    Post post = postRepository.findById(id).orElseThrow();

    checkPostCommentPermission(post);

    Comment comment = new Comment();
    comment.setPost(post);
    comment.setAuthor(user);

    if (commentRequest.getParentId() != null) {
      comment.setType(CommentType.COMMENT);
      String text = commentRequest.getCommentText().substring(",message:".length());
      comment.setCommentText(text);
      Comment parentComment = commentRepository
          .findById(commentRequest.getParentId())
          .orElseThrow();

      checkCommentCommentPermission(parentComment);

      comment.setParent(parentComment);
    } else {
      comment.setType(CommentType.POST);
      comment.setCommentText(commentRequest.getCommentText());
    }
    comment = commentRepository.save(comment);

    notificationService.saveComment(comment);

    return getCommentResponse(comment, user);
  }

  private void checkPostCommentPermission(Post post) {
    User author = post.getAuthor();
    NotificationSetting setting = notificationSettingService.getSetting(author);

    if (setting.getPostCommentEnabled().equals(Boolean.FALSE)) {
      throw new NotificationPermissionException(String.format(
          "Пользователь %s запретил комментировать свои публикации", author.getLastName()
      ));
    }
  }

  private void checkCommentCommentPermission(Comment parentComment) {
    User author = parentComment.getAuthor();

    NotificationSetting setting = notificationSettingService.getSetting(author);

    if (setting.getCommentCommentEnabled().equals(Boolean.FALSE)) {
      throw new NotificationPermissionException(String.format(
          "Пользователь %s запретил комментировать свои комментарии", author
      ));
    }
  }

  public CommonResponseDTO<CommentDTO> getCommentResponse(Comment comment, User user) {
    CommonResponseDTO<CommentDTO> commentResponse = new CommonResponseDTO<>();
    commentResponse.setTimestamp(LocalDateTime.now());
    commentResponse.setData(getCommentDTO(comment, user));
    return commentResponse;
  }

  private CommonListResponseDTO<CommentDTO> getPostResponse(int offset, int itemPerPage, Page<Comment> pageableCommentList, User user) {

    return CommonListResponseDTO.<CommentDTO>builder()
        .total(pageableCommentList.getTotalElements())
        .perPage(itemPerPage)
        .offset(offset)
        .data(getCommentDTO4Response(pageableCommentList.toSet(), user))
        .build();

  }

  public CommentDTO getCommentDTO(Comment comment, User user) {

    CommentDTO commentDTO = new CommentDTO();
    commentDTO.setCommentText(comment.getCommentText());
    commentDTO.setBlocked(comment.getIsBlocked());
    commentDTO.setAuthor(userMapper.userToUserDTO(user));

    commentDTO.setId(comment.getId());
    commentDTO.setTime(comment.getTime());

    if (comment.getParent() != null) {
      commentDTO.setParentId(comment.getParent().getId());
      commentDTO.setType(CommentType.COMMENT);
      commentDTO.setCommentText(comment.getCommentText());
    } else {
      commentDTO.setType(CommentType.POST);
      commentDTO.setCommentText(comment.getCommentText());
    }

    commentDTO.setDeleted(comment.getIsDeleted());
    commentDTO.setPost(comment.getPost().getId());
    commentDTO.setSubComments(new ArrayList<>());

    List<StorageDTO> images = new ArrayList<>();
    commentDTO.setImages(images);
    return commentDTO;
  }

  public CommonResponseDTO<CommentDTO> deleteComment(long id) {
    User user = CurrentUserUtils.getCurrentUser();
    Comment comment = commentRepository.findById(id).orElseThrow();
    if (!user.getId().equals(comment.getAuthor().getId())) {
      comment.setIsDeleted(true);
    }
    commentRepository.saveAndFlush(comment);
    return getCommentResponse(comment, user);
  }


  public CommonResponseDTO<CommentDTO> recoveryComment(int id) {
    User user = CurrentUserUtils.getCurrentUser();
    Comment comment = findComment(id);
    comment.setIsDeleted(!Objects.equals(comment.getAuthor().getId(), user.getId()) && comment.getIsDeleted());
    commentRepository.saveAndFlush(comment);
    return getCommentResponse(comment, user);
  }


  public CommonResponseDTO<CommentDTO> putComment(long id, long commentId, CommentRequestDTO commentRequest) {
    User user = CurrentUserUtils.getCurrentUser();
    if (postRepository.findById(id).isEmpty()) {
      throw new CommentNotFoundException("Не найден пост с id = " + id);
    }
    if (commentRequest.getParentId() != null)
      findComment(commentRequest.getParentId());
    Comment comment = findComment(commentId);
    comment.setCommentText(commentRequest.getCommentText());
    commentRepository.save(comment);

    return getCommentResponse(comment, user);
  }

  private Comment findComment(long id) throws CommentNotFoundException {
    return commentRepository.findById(id)
        .orElseThrow();
  }
}
