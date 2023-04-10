package ru.skillbox.zerone.backend.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.CommentNotFoundException;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.request.CommentRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommentDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.StorageDTO;
import ru.skillbox.zerone.backend.model.entity.Comment;
import ru.skillbox.zerone.backend.model.entity.Post;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.model.enumerated.CommentType;
import ru.skillbox.zerone.backend.repository.CommentRepository;
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

  public CommonListResponseDTO<CommentDTO> getPage4Comments(int offset, int itemPerPage, Post post) {

    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
    Page<Comment> pageableCommentList = commentRepository
        .findCommentsByPostIdAndParentNull(post.getId(), pageable);

    return getPostResponse(offset, itemPerPage, pageableCommentList);
  }

  public CommonListResponseDTO<CommentDTO> getComments(int offset, int itemPerPage, long id) {
    Post post = postRepository.findById(id).orElseThrow();
    return getPage4Comments(offset, itemPerPage, post);
  }

  public List<CommentDTO> getCommentDTO4Response(Set<Comment> comments) {
    List<CommentDTO> commentDTOList = new ArrayList<>();
    comments.forEach(comment -> {
      CommentDTO commentData = getCommentDTO(comment);
      commentData.getSubComments().add(getCommentDTO(comment));
      commentDTOList.add(commentData);
    });
    return new ArrayList<>(commentDTOList);
  }

  public CommonResponseDTO<CommentDTO> addComment(long id, CommentRequestDTO commentRequest) {
    User user = CurrentUserUtils.getCurrentUser();
    Post post = postRepository.findById(id).orElseThrow();
    Comment comment = new Comment();
    comment.setCommentText(commentRequest.getCommentText());
    comment.setPost(post);

    if (commentRequest.getParentId() != null) {
      Comment parentComment = commentRepository
          .findById(commentRequest.getParentId()).orElseThrow();
      comment.setParent(parentComment);
      comment.setCommentText(commentRequest.getCommentText());
      comment.setType(CommentType.COMMENT);
    } else {
      comment.setCommentText(commentRequest.getCommentText());
      comment.setType(CommentType.POST);
    }
    comment.setPost(post);
    comment.setTime(LocalDateTime.now());
    comment.setAuthor(user);
    comment = commentRepository.save(comment);

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
        .data(getCommentDTO4Response(pageableCommentList.toSet()))
        .build();

  }

  public CommentDTO getCommentDTO(Comment comment) {

    CommentDTO commentDTO = new CommentDTO();
    commentDTO.setCommentText(comment.getCommentText());
    commentDTO.setBlocked(comment.getIsBlocked());
    commentDTO.setAuthor(userMapper.userToUserDTO(comment.getAuthor()));

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
    return getCommentResponse(comment);
  }


  public CommonResponseDTO<CommentDTO> recoveryComment(int id) {
    User user = CurrentUserUtils.getCurrentUser();
    Comment comment = findComment(id);
    comment.setIsDeleted(!Objects.equals(comment.getAuthor().getId(), user.getId()) && comment.getIsDeleted());
    commentRepository.saveAndFlush(comment);
    return getCommentResponse(comment);
  }


  public CommonResponseDTO<CommentDTO> putComment(long id, long commentId, CommentRequestDTO commentRequest) {

    postRepository.findById(id).orElseThrow();
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
//      comment.getComments()
  //          .forEach(pomment -> commentData.getSubComments().add(getCommentDTO(pomment, user)));
}
