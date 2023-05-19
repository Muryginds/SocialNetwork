package ru.skillbox.zerone.backend.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.CommentNotFoundException;
import ru.skillbox.zerone.backend.exception.PostNotFoundException;
import ru.skillbox.zerone.backend.exception.UserAndAuthorNotEqualsException;
import ru.skillbox.zerone.backend.mapstruct.CommentMapper;
import ru.skillbox.zerone.backend.model.dto.request.CommentRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommentDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.entity.Comment;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.repository.CommentRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class CommentService {
  private final CommentRepository commentRepository;
  private final NotificationService notificationService;
  private final CommentMapper commentMapper;

  public CommonListResponseDTO<CommentDTO> getComments(int offset, int itemPerPage, long id) {
    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage, Sort.by(Sort.Direction.ASC, "time"));
    Page<Comment> commentPage = commentRepository.findCommentsByPostIdAndParentIsNull(id, pageable);
    return getCommentResponse(offset, itemPerPage, commentPage);
  }

  public CommonResponseDTO<CommentDTO> addComment(long postId, CommentRequestDTO commentRequest) {
    Comment comment = commentMapper.dtoToComment(commentRequest, postId);
    commentRepository.save(comment);
    notificationService.saveComment(comment);
    return getCommentResponse(comment);
  }

  public CommonResponseDTO<CommentDTO> deleteComment(long id) {
    Comment comment = commentRepository.findById(id).orElseThrow(createCommentNotFoundException(id));
    return setCommentIsDeleted(comment, true);
  }

  public CommonResponseDTO<CommentDTO> recoveryComment(long id) {
    Comment comment = commentRepository.findById(id).orElseThrow(createCommentNotFoundException(id));
    return setCommentIsDeleted(comment, false);
  }

  public CommonResponseDTO<CommentDTO> updateComment(long postId, long commentId, CommentRequestDTO commentRequest) {
    Comment commentForEdit = commentRepository.findById(commentId).orElseThrow(createCommentNotFoundException(commentId));
    if (!commentForEdit.getPost().getId().equals(postId)) {
      throw new PostNotFoundException(String.format("Комментарий с id %d не принадлежит посту с id %d", commentId, postId));
    }
    commentMapper.updateComment(commentForEdit, commentRequest);
    commentRepository.save(commentForEdit);
    return getCommentResponse(commentForEdit);
  }

  private Supplier<CommentNotFoundException> createCommentNotFoundException(long id) {
    return () -> new CommentNotFoundException(String.format("Комментарий с id %d не найден", id));
  }

  private CommonResponseDTO<CommentDTO> getCommentResponse(Comment comment) {
    return CommonResponseDTO.<CommentDTO>builder()
        .data(commentMapper.commentToCommentDTO(comment))
        .build();
  }

  private CommonListResponseDTO<CommentDTO> getCommentResponse(int offset, int itemPerPage, Page<Comment> pageableCommentList) {
    return CommonListResponseDTO.<CommentDTO>builder()
        .total(pageableCommentList.getTotalElements())
        .perPage(itemPerPage)
        .offset(offset)
        .data(commentMapper.commentListToCommentDTOList(pageableCommentList.toList()))
        .build();
  }

  private CommonResponseDTO<CommentDTO> setCommentIsDeleted(Comment comment, boolean isDeleted) {
    User user = CurrentUserUtils.getCurrentUser();
    if (user.getId().equals(comment.getAuthor().getId())) {
      comment.setIsDeleted(isDeleted);
      commentRepository.saveAndFlush(comment);
    } else {
      throw new UserAndAuthorNotEqualsException("Нельзя изменять комментарий, автором которого вы не являетесь");
    }
    return getCommentResponse(comment);
  }

}
