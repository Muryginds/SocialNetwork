package ru.skillbox.zerone.backend.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.CommentNotFoundException;
import ru.skillbox.zerone.backend.mapstruct.CommentMapper;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.request.CommentRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommentDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.StorageDTO;
import ru.skillbox.zerone.backend.model.entity.Comment;
import ru.skillbox.zerone.backend.model.entity.Like;
import ru.skillbox.zerone.backend.model.entity.Post;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.model.enumerated.CommentType;
import ru.skillbox.zerone.backend.model.enumerated.LikeType;
import ru.skillbox.zerone.backend.repository.CommentRepository;
import ru.skillbox.zerone.backend.repository.LikeRepository;
import ru.skillbox.zerone.backend.repository.PostRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommentService {
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final UserMapper userMapper;
  private final LikeRepository likeRepository;

  private final CommentMapper commentMapper;
  private final NotificationService notificationService;

  public CommonListResponseDTO<CommentDTO> getPage4Comments(int offset, int itemPerPage, Post post) {

    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage).withSort(Sort.by("time").ascending());
    Page<Comment> pageableCommentList = commentRepository
        .findCommentsByPostIdAndParentNull(post.getId(), pageable);

    return getPostResponse(offset, itemPerPage, pageableCommentList);
  }

  public CommonListResponseDTO<CommentDTO> getComments(int offset, int itemPerPage, long id) {

    Post post = postRepository.findById(id).orElseThrow();
    return getPage4Comments(offset, itemPerPage, post);
  }

  public List<CommentDTO> getCommentDTO4Response(List<Comment> comments) {
    List<CommentDTO> commentDTOList = new ArrayList<>();
    comments.forEach(comment -> {
      CommentDTO commentData = getCommentDTO(comment);
      commentData.setSubComments(comment.getComments().stream().map(c -> getCommentDTO(c)).toList());
      commentDTOList.add(commentData);
    });
    return new ArrayList<>(commentDTOList);
  }

  public CommonResponseDTO<CommentDTO> addComment(long id, CommentRequestDTO commentRequest) {
    User user = CurrentUserUtils.getCurrentUser();
    Post post = postRepository.findById(id).orElseThrow();

    Comment comment = new Comment();
    comment.setPost(post);
    comment.setAuthor(user);
    comment.setCommentText(commentRequest.getCommentText());
    if (commentRequest.getParentId() != null) {
      comment.setType(CommentType.COMMENT);
      Comment parentComment = commentRepository
          .findById(commentRequest.getParentId())
          .orElseThrow();

      comment.setParent(parentComment);
    } else {
      comment.setType(CommentType.POST);
    }
    comment = commentRepository.save(comment);

    notificationService.saveComment(comment);

    return getCommentResponse(comment);
  }

  public CommonResponseDTO<CommentDTO> getCommentResponse(Comment comment) {
    CommonResponseDTO<CommentDTO> commentResponse = new CommonResponseDTO<>();
    commentResponse.setTimestamp(LocalDateTime.now());
    commentResponse.setData(getCommentDTO(comment));
    return commentResponse;
  }

  private CommonListResponseDTO<CommentDTO> getPostResponse(int offset, int itemPerPage, Page<Comment> pageableCommentList) {

    return CommonListResponseDTO.<CommentDTO>builder()
        .total(pageableCommentList.getTotalElements())
        .perPage(itemPerPage)
        .offset(offset)
        .data(getCommentDTO4Response(pageableCommentList.toList()))
        .build();

  }

  public CommentDTO getCommentDTO(Comment comment) {
    CommentDTO commentDTO = commentMapper.commentToCommentDTO(comment);
    commentDTO.setCommentText(comment.getCommentText());
    commentDTO.setDeleted(comment.getIsDeleted());
    commentDTO.setBlocked(comment.getIsBlocked());
    commentDTO.setAuthor(userMapper.userToUserDTO(comment.getAuthor()));

    if (comment.getParent() != null) {
      commentDTO.setParentId(comment.getParent().getId());
      commentDTO.setType(CommentType.COMMENT);
      commentDTO.setCommentText(comment.getCommentText());
    } else {
      commentDTO.setType(CommentType.POST);
      commentDTO.setCommentText(comment.getCommentText());
    }

    Set<Like> likes = likeRepository.findLikesByCommentIdAndType(comment.getId(), LikeType.COMMENT);
    commentDTO.setLikes(likes.size());
    commentDTO.setMyLike(likes.stream()
        .anyMatch(commentLike -> commentLike.getUser().getId().equals(CurrentUserUtils.getCurrentUser().getId())));


    List<StorageDTO> images = new ArrayList<>();
    commentDTO.setImages(images);
    return commentDTO;
  }

  public CommonResponseDTO<CommentDTO> deleteComment(long id) {
    User user = CurrentUserUtils.getCurrentUser();
    Comment comment = commentRepository.findById(id).orElseThrow();
    if (user.getId().equals(comment.getAuthor().getId())) {
      comment.setIsDeleted(true);
      commentRepository.saveAndFlush(comment);
    }
    return getCommentResponse(comment);
  }


  public CommonResponseDTO<CommentDTO> recoveryComment(long id) {
    User user = CurrentUserUtils.getCurrentUser();
    Comment comment = findComment(id);
    if (user.getId().equals(comment.getAuthor().getId())) {
      comment.setIsDeleted(false);
      commentRepository.saveAndFlush(comment);
    }
    return getCommentResponse(comment);
  }


  public CommonResponseDTO<CommentDTO> putComment(long id, long commentId, CommentRequestDTO commentRequest) {

    if (postRepository.findById(id).isEmpty()) {
      throw new CommentNotFoundException("Не найден пост с id = " + id);
    }
    if (commentRequest.getParentId() != null)
      findComment(commentRequest.getParentId());
    Comment comment = findComment(commentId);
    comment.setCommentText(commentRequest.getCommentText());
    commentRepository.save(comment);

    return getCommentResponse(comment);
  }

  private Comment findComment(long id) throws CommentNotFoundException {
    return commentRepository.findById(id)
        .orElseThrow();
  }
}
