package ru.skillbox.zerone.backend.mapstruct;

import org.springframework.beans.factory.annotation.Autowired;
import ru.skillbox.zerone.backend.model.dto.response.PostDTO;
import ru.skillbox.zerone.backend.model.entity.Like;
import ru.skillbox.zerone.backend.model.entity.Post;
import ru.skillbox.zerone.backend.model.entity.Tag;
import ru.skillbox.zerone.backend.model.enumerated.PostType;
import ru.skillbox.zerone.backend.repository.LikeRepository;
import ru.skillbox.zerone.backend.service.CommentService;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class PostMapperDecorator implements PostMapper {

  @Autowired
  private LikeRepository likeRepository;

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private CommentService commentService;

  @Override
  public PostDTO postToPostsDTO(Post post) {

    PostDTO postDTO = new PostDTO();
    Set<Like> likes = likeRepository.findLikesByPost(post);
    List<Tag> tagsOfPost = post.getTags();

    postDTO.setId(post.getId());
    postDTO.setPostText(post.getPostText());
    postDTO.setTitle(post.getTitle());
    postDTO.setAuthor(userMapper.userToUserDTO(post.getAuthor()));

    if (Boolean.TRUE.equals(post.getIsDeleted())) {
      postDTO.setType(PostType.DELETED);
    } else if (post.getUpdateTime().isBefore(post.getTime())) {
      postDTO.setType(PostType.QUEUED);
    } else {
      postDTO.setType(PostType.POSTED);
    }

    if (likes != null) {

      postDTO.setLikes(likes.size());

      likes.forEach(like -> {
        if (like.getUser().getId().equals(CurrentUserUtils.getCurrentUser().getId())) {
          postDTO.setMyLike(Boolean.TRUE);
        }
      });
    }

    postDTO.setBlocked(post.getIsBlocked());
    postDTO.setTime(convertLocalDateTimeToLong(post.getTime()));
    postDTO.setComments(commentService.getPage4Comments(0, 5, post));

    postDTO.setTags(tagsOfPost == null ? Collections.emptyList()
        : tagsOfPost.stream().map(Tag::getName).toList());

    return postDTO;
  }
}
