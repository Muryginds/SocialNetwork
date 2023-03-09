package ru.skillbox.zerone.backend.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.request.CommentRequest;
import ru.skillbox.zerone.backend.model.dto.response.*;
import ru.skillbox.zerone.backend.model.entity.Comment;
import ru.skillbox.zerone.backend.model.entity.Post;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.model.enumerated.CommentType;
import ru.skillbox.zerone.backend.repository.*;
import ru.skillbox.zerone.backend.exception.PostNotFoundException;
import ru.skillbox.zerone.backend.exception.CommentNotFoundException;
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

  public CommonListDTO<CommentDTO> getPage4Comments(int offset, int itemPerPage, Post post,User user) {

    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
    Page<Comment> pageableCommentList = commentRepository
        .findCommentsByPostIdAndParentIsNull(post.getId(), pageable);

    return getPostResponse(offset, itemPerPage, pageableCommentList, user);
  }
   public CommonListDTO<CommentDTO> getComments (int offset, int itemPerPage, long id) throws PostNotFoundException {
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

  public CommonResponseDTO<CommentDTO> comment(long id, CommentRequest commentRequest) throws PostNotFoundException, CommentNotFoundException {
    User user = CurrentUserUtils.getCurrentUser();
    Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
    Comment comment = new Comment();
    if (commentRequest.getParentId() != null) {
       Comment parentComment = commentRepository
          .findById(commentRequest.getParentId()).orElseThrow(CommentNotFoundException::new);
      comment.setParent(parentComment);
    }
    comment.setCommentText(commentRequest.getCommentText());
    comment.setPost(post);
    comment.setType(CommentType.POST);
    comment.setTime(LocalDateTime.now());
    comment.setAuthor(user);
    comment = commentRepository.save(comment);
//    if (commentRequest.getImages() != null) {
//      int id = comment.getId();
//      commentRequest.getImages().forEach(image -> fileRepository.save(fileRepository.findByUrl(image.getUrl()).setCommentId(id)));
//    }
//    sendNotification(comment);
    return getCommentResponse(comment, user);
  }
    public CommonResponseDTO<CommentDTO> getCommentResponse (Comment comment, User user){
      CommonResponseDTO<CommentDTO> commentResponse = new CommonResponseDTO<>();
      commentResponse.setTimestamp(LocalDateTime.now());
      commentResponse.setData(getCommentDTO(comment, user));
      return commentResponse;
    }
      private CommonListDTO<CommentDTO> getPostResponse(int offset, int itemPerPage, Page<Comment> pageableCommentList, User user) {
        return CommonListDTO.<CommentDTO>builder()
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
      commentDTO.setAuthor(userMapper.userToUserDTO(user));

//   а здесь нужно было засетать Автора отфильтрованного!!
      commentDTO.setId(comment.getId());
      commentDTO.setTime(comment.getTime());

//      Здесь должны быть лайки!!

      if (comment.getParent() != null)
        commentDTO.setParentId(comment.getParent().getId());
      commentDTO.setPostId(comment.getPost().getId());
      commentDTO.setSubComments(new ArrayList<>());
      List<ImageDTO> images = new ArrayList<>();
      commentDTO.setImages(images);
      return commentDTO;
    }

  public CommonResponseDTO<CommentDTO> deleteComment(long commentId) {
    User user = CurrentUserUtils.getCurrentUser();
    Comment comment = findComment(commentId);
    comment.setIsDeleted(Objects.equals(comment.getAuthor().getId(), user.getId()) || comment.getIsDeleted());
//    comment.setDeletedTimestamp(LocalDateTime.now());
    commentRepository.save(comment);
    return getCommentResponse(comment, user);
  }


  public CommonResponseDTO<CommentDTO> recoveryComment(int commentId) {
    User user = CurrentUserUtils.getCurrentUser();
    Comment comment = findComment(commentId);
    comment.setIsDeleted(!Objects.equals(comment.getAuthor().getId(), user.getId()) && comment.getIsDeleted());
    commentRepository.save(comment);
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
//    if (commentRequest.getImages() != null) {
//      int id = comment.getId();
//      commentRequest.getImages().forEach(image -> fileRepository.save(fileRepository.findByUrl(image.getUrl()).setCommentId(id)));
//    }
    return getCommentResponse(comment, user);
  }

  private Comment findComment(long itemId) throws CommentNotFoundException {
    return commentRepository.findById(itemId)
        .orElseThrow(CommentNotFoundException::new);
  }

}
