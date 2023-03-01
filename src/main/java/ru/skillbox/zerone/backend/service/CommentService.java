package ru.skillbox.zerone.backend.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.request.CommentRequest;
import ru.skillbox.zerone.backend.model.dto.response.*;
import ru.skillbox.zerone.backend.model.dto.UserDTO;
import ru.skillbox.zerone.backend.model.entity.Comment;
import ru.skillbox.zerone.backend.model.entity.Like;
import ru.skillbox.zerone.backend.model.entity.Post;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.model.enumerated.CommentType;
import ru.skillbox.zerone.backend.repository.*;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;
import ru.skillbox.zerone.backend.exception.PostNotFoundException;
import ru.skillbox.zerone.backend.exception.CommentNotFoundException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@AllArgsConstructor
public class CommentService {
  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final CommentRepository commentRepository;
  private final FileRepository fileRepository;
  private final LikeRepository likeRepository;
  private final FriendshipService friendshipService;
  private final UserService userService;
  private UserMapper userMapper;

  public CommonListDTO<CommentDTO> getPage4Comments(int offset, int itemPerPage, Post post,User user) {

    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
    Page<Comment> pageableCommentList = commentRepository
        .findCommentsByPostIdAndParentIsNull(post.getId(), pageable);

    return getPostResponse(offset, itemPerPage, pageableCommentList, user);
  }
   public CommonListDTO<CommentDTO> getComments (int offset, int itemPerPage, int id, Principal principal) throws PostNotFoundException {
    User user = findUser(principal.getName());
    Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
    return getPage4Comments(offset, itemPerPage, post,user);
  }
  public List<CommentDTO> getCommentDTO4Response(Set<Comment> comments, User user) {
    List<CommentDTO> commentDTOList = new ArrayList<>();
    comments.forEach(comment -> {
      CommentDTO commentData = getCommentDTO(comment, user);
//      comment.getComments()
//          .forEach(subcomment -> commentData.getSubComments().add(getCommentDTO(comment, user)));
      commentDTOList.add(commentData);
    });
    return new ArrayList<>(commentDTOList);
  }
//    comments.forEach(comment -> {
//      CommentDTO commentData = getCommentDTO(comment, user);
//      commentDTOList.add(commentData);
//    });

  public CommonResponseDTO<CommentDTO> comment(int id, CommentRequest commentRequest, Principal principal) throws PostNotFoundException, CommentNotFoundException {
    User user = findUser(principal.getName());
    Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
    Comment comment = new Comment();
//    if (commentRequest.getParentId() != null) {
//       Comment parentComment = commentRepository
//          .findById(commentRequest.getParentId()).orElseThrow(CommentNotFoundException::new);
//      comment.setParent(parentComment);
//    }
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
    private User findUser (String email) {
      return userRepository.findUserByEmail(email)
          .orElseThrow(() -> new UsernameNotFoundException(email));
    }
      private CommonListDTO<CommentDTO> getPostResponse(int offset, int itemPerPage, Page<Comment> pageableCommentList, User user) {
    CommonListDTO<CommentDTO> commentResponse = new CommonListDTO<>();
        commentResponse.setPerPage(itemPerPage);
        commentResponse.setTimestamp(LocalDateTime.now());
        commentResponse.setOffset(offset);
        commentResponse.setTotal((int) pageableCommentList.getTotalElements());
        commentResponse.setData(getCommentDTO4Response(pageableCommentList.toSet(), user));
    return commentResponse;
  }
    public CommentDTO getCommentDTO (Comment comment, User user) {
//      User user = CurrentUserUtils.getCurrentUser();
      CommentDTO commentDTO = new CommentDTO();
      commentDTO.setCommentText(comment.getCommentText());
      commentDTO.setAuthor(userMapper.userToUserDTO(user));

//      commentDTO.setBlocked(ru.skillbox.zerone.backend.model.entity.Comment.isBlocked(false));
//    if (comment.getUserMapper().isDeleted()) {
//      commentDTO.setAuthor(setDeletedUserDTO(comment.getUser()));
//  } else if (comment.getUser().getId().equal(comment.getUser().getId()) || !friendshipService.isBlockedBy(comment.getUser().getId(), user.getId())) {
//    commentDTO.setAuthor(setUserDTO(comment.getUser()));
//  } else {
//    commentDTO.setAuthor(setBlockerUserDTO(comment.getUser()));
//  }

      commentDTO.setId(comment.getId());
      commentDTO.setTime(comment.getTime());
//      commentDTO.setDeleted(comment.isDeleted());
//      Set<Like> likes = likeRepository.findLikeByIdAndType(comment.getId(), "Comment");
//      commentDTO.setLikes(likes.size());
//      commentDTO.setMyLike(likes.stream()
//          .anyMatch(commentLike -> commentLike.getUser().equals(user)));
      if (comment.getParent() != null)
        commentDTO.setParentId(comment.getParent().getId());
      commentDTO.setPostId(comment.getPost().getId());
      commentDTO.setSubComments(new ArrayList<>());
      List<ImageDTO> images = new ArrayList<>();
      commentDTO.setImages(images);
      return commentDTO;
    }

  public CommonResponseDTO<CommentDTO> deleteComment(int commentId, Principal principal) {
    User user = findUser(principal.getName());
    Comment comment = findComment(commentId);
    comment.setIsDeleted(Objects.equals(comment.getAuthor().getId(), user.getId()) || comment.getIsDeleted());
//    comment.setDeletedTimestamp(LocalDateTime.now());
    commentRepository.save(comment);
    return getCommentResponse(comment, user);
  }


  public CommonResponseDTO<CommentDTO> recoveryComment(int commentId, Principal principal) {
    User user = findUser(principal.getName());
    Comment comment = findComment(commentId);
    comment.setIsDeleted(!Objects.equals(comment.getAuthor().getId(), user.getId()) && comment.getIsDeleted());
    commentRepository.save(comment);
    return getCommentResponse(comment, user);
  }


  public CommonResponseDTO<CommentDTO> putComment(int id, int commentId, CommentRequest commentRequest, Principal principal) {
    return null;
  }

  private Comment findComment(int itemId) throws CommentNotFoundException {
    return commentRepository.findById(itemId)
        .orElseThrow(CommentNotFoundException::new);
  }

}
