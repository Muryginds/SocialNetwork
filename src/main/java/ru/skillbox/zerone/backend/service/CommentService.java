package ru.skillbox.zerone.backend.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.dto.response.CommentDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListDTO;
import ru.skillbox.zerone.backend.model.entity.Post;
import ru.skillbox.zerone.backend.repository.FileRepository;
import ru.skillbox.zerone.backend.repository.LikeRepository;
import ru.skillbox.zerone.backend.repository.PostRepository;

@Service
@AllArgsConstructor
public class CommentService {
  private final PostRepository postRepository;
  private final FileRepository fileRepository;
  private final LikeRepository likeRepository;
  private final FriendshipService friendshipService;

  public CommonListDTO<CommentDTO> getPage4PostComments(int offset, int itemPerPage, Post post) {
//
//    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
//    Page<PostComment> pageablePostCommentList = commentRepository
//        .findPostCommentsByPostIdAndParentIsNullOrderByTime(post.getId(), pageable);
return null;
//    return getPostResponse(offset, itemPerPage, pageablePostCommentList);
  }

//  private CommonListDTO<CommentDTO> getPostResponse(int offset, int itemPerPage, Page<PostComment> pageablePostCommentList) {
//
//    CommonListDTO<CommentDTO> postCommentResponse = new CommonListDTO<>();
//    postCommentResponse.setPerPage(itemPerPage);
//    postCommentResponse.setTimestamp(LocalDateTime.now());
//    postCommentResponse.setOffset(offset);
//    postCommentResponse.setTotal((int) pageablePostCommentList.getTotalElements());
//    postCommentResponse.setData(getCommentDTO4Response(pageablePostCommentList.toSet()));
//    return postCommentResponse;
//  }

//  public List<CommentDTO> getCommentDTO4Response(Set<PostComment> comments) {

//    List<CommentDTO> commentDataList = new ArrayList<>();
//    comments.forEach(postComment -> {
//      CommentDTO commentData = getCommentDTO(postComment);
//      postComment.getPostComments()
//          .forEach(comment -> commentData.getSubComments().add(getCommentDTO(comment)));
//      commentDataList.add(commentData);
//    });
//    return new ArrayList<>(commentDataList);
//  }

//  public CommonResponseDTO<CommentDTO> postComment (int itemId, CommentRequest commentRequest, Principal principal) throws PostNotFoundException, CommentNotFoundException {
//
//    Post post = postRepository.findById(itemId).orElseThrow(PostNotFoundException::new);
//    PostComment postComment = new PostComment();
//    if (commentRequest.getParentId() != null) {
//      PostComment parentPostComment = commentRepository
//          .findById(commentRequest.getParentId()).orElseThrow(CommentNotFoundException::new);
//      postComment.setParent(parentPostComment);
//    }
//    postComment.setCommentText(commentRequest.getCommentText());
//    postComment.setPost(post);
//    postComment.setTime(LocalDateTime.now());
//    postComment.setUser(User.builder().build());
//    postComment = commentRepository.save(postComment);
//    if (commentRequest.getImages() != null) {
//      int id = postComment.getId();
//      commentRequest.getImages().forEach(image -> fileRepository.save(fileRepository.findByUrl(image.getUrl()).setCommentId(id)));
//    }
//
//    sendNotification(postComment);
//    return getCommentResponse(postComment);
//  }
//  public CommonResponseDTO<CommentDTO> getCommentResponse(PostComment postComment) {
//    CommonResponseDTO<CommentDTO> commentResponse = new CommonResponseDTO<>();
//    commentResponse.setTimestamp(LocalDateTime.now());
//    commentResponse.setData(getCommentDTO(postComment));
//    return commentResponse;
//  }

  //  private CommonListDTO<CommentDTO> getPostResponse(int offset, int itemPerPage, Page<PostComment> pageablePostCommentList, Person person) {
//    CommonListDTO<CommentDTO> postCommentResponse = new CommonResponseDTO<>();
//    postCommentResponse.setPerPage(itemPerPage);
//    postCommentResponse.setTimestamp(LocalDateTime.now());
//    postCommentResponse.setOffset(offset);
//    postCommentResponse.setTotal((int) pageablePostCommentList.getTotalElements());
//    postCommentResponse.setData(getCommentDTO4Response(pageablePostCommentList.toSet(), person));
//    return postCommentResponse;
//  }
//  public CommentDTO getCommentDTO(PostComment postComment) {
//    User user = CurrentUserUtils.getCurrentUser();
//    CommentDTO commentData = new CommentDTO();
//    commentData.setCommentText(postComment.getCommentText());
//
//    commentData.setBlocked(postComment.isBlocked());
////    if (postComment.getUser().isDeleted()) {
////      commentData.setAuthor(setDeletedUserDTO(postComment.getUser()));
////  } else if (postComment.getUser().getId().equals(postComment.getUser().getId()) || !friendshipService.isBlockedBy(postComment.getUser().getId(), user.getId())) {
////    commentData.setAuthor(setUserDTO(postComment.getUser()));
////  } else {
////    commentData.setAuthor(setBlockerUserDTO(postComment.getUser()));
////  }
//
//  commentData.setId(postComment.getId());
//  commentData.setTime(postComment.getTime());
//  commentData.setDeleted(postComment.isDeleted());
//  Set<Like> likes = likeRepository.findLikesByItemAndType(postComment.getId(), "Comment");
//  commentData.setLikes(likes.size());
//  commentData.setMyLike(likes.stream()
//      .anyMatch(commentLike -> commentLike.getUser().equals(user)));
//  if (postComment.getParent() != null)
//    commentData.setParentId(postComment.getParent().getId());
//  commentData.setPostId(postComment.getPost().getId());
//  commentData.setSubComments(new ArrayList<>());
////  List<ImageDTO> images = fileRepository.findAll().stream()
////      .filter(f -> f.getId() != null)
////      .filter(file -> file.getId().equals(postComment.getId()))
////      .map(file -> new ImageDTO().setId(String.valueOf(file.getId())).setId.
////      .collect(Collectors.toList());
////  commentData.setImages(images);
//      return commentData;
//    }

}