package ru.skillbox.zerone.backend.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.CommentNotFoundException;
import ru.skillbox.zerone.backend.exception.PostNotFoundException;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.request.CommentRequest;
import ru.skillbox.zerone.backend.model.dto.response.CommentDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.ImageDTO;
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

  public CommonListResponseDTO<CommentDTO> getPage4Comments(int offset, int itemPerPage, Post post,User user) {

    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
    Page<Comment> pageableCommentList = commentRepository
        .findCommentsByPostId(post.getId(), pageable);

    return getPostResponse(offset, itemPerPage, pageableCommentList, user);
  }
   public CommonListResponseDTO<CommentDTO> getComments (int offset, int itemPerPage, long id) throws PostNotFoundException {
     User user = CurrentUserUtils.getCurrentUser();
    Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
    return getPage4Comments(offset, itemPerPage, post,user);
  }
  public List<CommentDTO> getCommentDTO4Response(Set<Comment> comments, User user) {
    List<CommentDTO> commentDTOList = new ArrayList<>();
    comments.forEach(comment -> {
      CommentDTO commentData = getCommentDTO(comment, user);

      //должны или не должны быть сабкомменты?

      commentDTOList.add(commentData);
    });
    return new ArrayList<>(commentDTOList);
  }

  public CommonResponseDTO<CommentDTO> addComment(long id, CommentRequest commentRequest) throws PostNotFoundException, CommentNotFoundException {
    User user = CurrentUserUtils.getCurrentUser();
    Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
    Comment comment = new Comment();
    comment.setCommentText(commentRequest.getCommentText());
    comment.setPost(post);

//    if (commentRequest.getParentId() != null)
    if (commentRequest.getParentId() != null) {
       Comment parentComment = commentRepository
          .findById(commentRequest.getParentId()).orElseThrow(CommentNotFoundException::new);
       comment.setParent(parentComment);
      comment.setCommentText(commentRequest.getCommentText());
       comment.setType(CommentType.COMMENT);
    }else {
      comment.setCommentText(commentRequest.getCommentText());
      comment.setType(CommentType.POST);
    }
    comment.setPost(post);
    comment.setTime(LocalDateTime.now());
    comment.setAuthor(user);
    comment = commentRepository.save(comment);
    // Тут должен быть лист Изображений
//    sendNotification(comment);
    return getCommentResponse(comment, user);
  }
    public CommonResponseDTO<CommentDTO> getCommentResponse (Comment comment, User user){
      CommonResponseDTO<CommentDTO> commentResponse = new CommonResponseDTO<>();
      commentResponse.setTimestamp(LocalDateTime.now());
      commentResponse.setData(getCommentDTO(comment, user));
      return commentResponse;
    }
      private CommonListResponseDTO<CommentDTO> getPostResponse(int offset, int itemPerPage, Page<Comment> pageableCommentList, User user) {

            return CommonListResponseDTO.<CommentDTO>builder()
            .total((int) pageableCommentList.getTotalElements())
            .perPage(itemPerPage)
            .offset(offset)
            .timestamp(LocalDateTime.now())
            .data(getCommentDTO4Response(pageableCommentList.toSet(), user))
            .build();

  }
    public CommentDTO getCommentDTO (Comment comment, User user) {

      CommentDTO commentDTO = new CommentDTO();
      commentDTO.setCommentText(comment.getCommentText());
//      commentDTO.setBlocked(comment.isBlocked());
      commentDTO.setAuthor(userMapper.userToUserDTO(user));
      //   а здесь нужно было засетать Автора отфильтрованного!!
      commentDTO.setId(comment.getId());
      commentDTO.setTime(comment.getTime());

//      Здесь должны быть лайки!!

      if (comment.getParent() != null) {
        commentDTO.setParentId(comment.getParent().getId());
        commentDTO.setType(CommentType.COMMENT);
        commentDTO.setCommentText(comment.getCommentText());
      }else {
        commentDTO.setType(CommentType.POST);
        commentDTO.setCommentText(comment.getCommentText());
            }

      commentDTO.setDeleted(comment.getIsDeleted());
      commentDTO.setPost(comment.getPost().getId());
      commentDTO.setSubComments(new ArrayList<>());
      List<ImageDTO> images = new ArrayList<>();
      commentDTO.setImages(images);
      return commentDTO;
    }

  public CommonResponseDTO<CommentDTO> deleteComment(long id) {
    User user = CurrentUserUtils.getCurrentUser();
//    Comment comment = findComment(commentId);
//    comment.setIsDeleted(Objects.equals(comment.getAuthor().getId(), user.getId()) || comment.getIsDeleted());
    Comment comment = commentRepository.findById(id).orElseThrow(PostNotFoundException::new);
    if (!user.getId().equals(comment.getAuthor().getId()));
    comment.setIsDeleted(true);
//      post.setIsDeletedTime(LocalDateTime.now());
    commentRepository.saveAndFlush(comment);
//    comment.setTime(LocalDateTime.now());
//    commentRepository.save(comment);
    return getCommentResponse(comment, user);
  }


  public CommonResponseDTO<CommentDTO> recoveryComment(int id) {
    User user = CurrentUserUtils.getCurrentUser();
    Comment comment = findComment(id);
    comment.setIsDeleted(!Objects.equals(comment.getAuthor().getId(), user.getId()) && comment.getIsDeleted());
    commentRepository.saveAndFlush(comment);
    return getCommentResponse(comment, user);
  }


  public CommonResponseDTO<CommentDTO> putComment(long id, long commentId, CommentRequest commentRequest) throws CommentNotFoundException, PostNotFoundException {
    User user = CurrentUserUtils.getCurrentUser();
    postRepository.findById(id).orElseThrow(PostNotFoundException::new);
    if (commentRequest.getParentId() != null)
      findComment(commentRequest.getParentId());
    Comment comment = findComment(commentId);
    comment.setCommentText(commentRequest.getCommentText());
    commentRepository.save(comment);

    // Тут должен быть лист Изображений

    return getCommentResponse(comment, user);
  }

  private Comment findComment(long id) throws CommentNotFoundException {
    return commentRepository.findById(id)
        .orElseThrow(CommentNotFoundException::new);
  }

//  public AddCommentResponse addComment(long post, CommentRequest commentRequest) {
//
//    User user = CurrentUserUtils.getCurrentUser();
//
//    Comment comment = Comment.builder()
//        .time(LocalDateTime.now(ZoneOffset.UTC))
//        .post(postRepository.getById(post))
//        .author(user)
//        .commentText(commentRequest.getCommentText())
//        .isBlocked(false)
//        .build();
//    if(commentRequest.getParentId() != null) {
//      comment.setParent(commentRepository.getById(commentRequest.getParentId()));
//
//    }
//
//    commentRepository.save(comment);
//
//    return AddCommentResponse.builder()
//        .error("string")
//        .timestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
//        .data(AddCommentResponse.Data.builder()
//            .id(comment.getId())
//            .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
//            .post(comment.getPost().getId())
//            .timestamp(comment.getTime().toEpochSecond(ZoneOffset.UTC))
//            .authorId(user.getId())
//            .commentText(comment.getCommentText())
////            .isBlocked(comment.isBlocked())
//            .build())
//        .build();
//  }


}
