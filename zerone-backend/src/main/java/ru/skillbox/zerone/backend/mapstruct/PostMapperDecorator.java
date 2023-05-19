package ru.skillbox.zerone.backend.mapstruct;

import org.springframework.beans.factory.annotation.Autowired;
import ru.skillbox.zerone.backend.model.dto.response.PostDTO;
import ru.skillbox.zerone.backend.model.entity.Post;
import ru.skillbox.zerone.backend.model.enumerated.PostType;
import ru.skillbox.zerone.backend.repository.LikeRepository;
import ru.skillbox.zerone.backend.service.CommentService;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.util.List;

public abstract class PostMapperDecorator implements PostMapper {

  @Autowired
  private LikeRepository likeRepository;

  @Autowired
  private PostMapper postMapper;

  @Autowired
  private CommentService commentService;

  @Override
  public PostDTO postToPostsDTO(Post post) {

    PostDTO postDTO = postMapper.postToPostsDTO(post);
    postDTO.setLikes(likeRepository.countByPost(post));
    postDTO.setType(getPostType(post));
    postDTO.setComments(commentService.getComments(0, 5, post.getId()));
    likeRepository.findLikesByPost(post).forEach(like -> {
      if (like.getUser().getId().equals(CurrentUserUtils.getCurrentUser().getId())) {
        postDTO.setMyLike(Boolean.TRUE);
      }
    });
    return postDTO;
  }

    private PostType getPostType(Post post) {
      if (Boolean.TRUE.equals(post.getIsDeleted())) {
        return PostType.DELETED;
      } else if (post.getUpdateTime().isBefore(post.getTime())) {
        return PostType.QUEUED;
      } else {
        return PostType.POSTED;
      }
    }

    @Override
    public List<PostDTO> toDtoList(List<Post> postList) {
      return postList.stream().map(this::postToPostsDTO).toList();
  }
}