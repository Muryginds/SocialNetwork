package ru.skillbox.zerone.admin.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.zerone.admin.model.dto.CommentDescriptionDto;
import ru.skillbox.zerone.admin.model.dto.CommentModerationDto;
import ru.skillbox.zerone.admin.model.dto.ErrorDto;
import ru.skillbox.zerone.admin.model.dto.TotalCommentDto;
import ru.skillbox.zerone.admin.model.entity.Comment;
import ru.skillbox.zerone.admin.model.entity.Post;
import ru.skillbox.zerone.admin.model.entity.User;
import ru.skillbox.zerone.admin.repository.CommentRepository;
import ru.skillbox.zerone.admin.repository.PostRepository;
import ru.skillbox.zerone.admin.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentsService {

  private static final String POST_AUTHOR_ERROR = "post_author_error";
  private static final String POST_PARAMETERS_ERROR = "post_parameters_error";
  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final CommentRepository commentRepository;

  public ErrorDto checkParams(CommentDescriptionDto descr) {
    if (descr.getPostAuthor().isEmpty()) {
      return new ErrorDto(POST_AUTHOR_ERROR, "Не задан автор поста");
    }
    Authors authors = getAuthors(descr);

    if (authors.postAuthorLastName.isEmpty()) {
      return new ErrorDto(POST_AUTHOR_ERROR, "Не задана фамилия автора поста");
    }

    if (descr.getPostTitle().isEmpty()) {
      return new ErrorDto(POST_AUTHOR_ERROR, "Не задан заголовок поста");
    }

    if (!descr.getCommentAuthor().isEmpty() && authors.commentAuthorLastName.isEmpty()) {
      return new ErrorDto(POST_AUTHOR_ERROR, "Не задана фамилия автора комментария");
    }

    List<User> postAuthors = userRepository.findAllByFirstNameAndLastName(
        authors.postAuthorFirstName, authors.postAuthorLastName);
    if (postAuthors.isEmpty()) {
      return new ErrorDto(POST_AUTHOR_ERROR, "Автор поста не найден");
    }

    List<Post> posts = postRepository.findAllByTitleAndAuthorIn(descr.getPostTitle(), postAuthors);
    if (posts.isEmpty()) {
      return new ErrorDto(POST_PARAMETERS_ERROR, "Пост с заданными параметрами не найден");
    }
    return null;
  }

  public TotalCommentDto postCommentDescription(CommentDescriptionDto descr) {
    Authors authors = getAuthors(descr);
    Pageable pageable = PageRequest.of(0, 5);

    Page<Comment> comments;
    List<User> postAuthors = userRepository.findAllByFirstNameAndLastName(
        authors.postAuthorFirstName, authors.postAuthorLastName);
    List<Post> posts = postRepository.findAllByTitleAndAuthorIn(descr.getPostTitle(), postAuthors);

    if (authors.commentAuthorFirstName.isEmpty()) {
      if (descr.getCommentFragment().isEmpty()) {
        comments = commentRepository.findAllByPostIn(posts, pageable);
      } else {
        comments = commentRepository.findAllByPostInAndCommentTextContains(
            posts, descr.getCommentFragment(), pageable);
      }
    } else {
      List<User> commentAuthors = userRepository.findAllByFirstNameAndLastName(
          authors.commentAuthorFirstName, authors.commentAuthorLastName);
      if (descr.getCommentFragment().isEmpty()) {
        comments = commentRepository.findAllByPostInAndAuthorIn(posts, commentAuthors, pageable);
      } else {
        comments = commentRepository.findAllByPostInAndAuthorInAndCommentTextContains(
            posts, commentAuthors, descr.getCommentFragment(), pageable);
      }
    }

    if (comments.getTotalElements() == 0) {
      return null;
    }

    List<CommentModerationDto> dtos = new ArrayList<>();
    comments.forEach(comment -> {
      CommentModerationDto dto = new CommentModerationDto()
          .setId(comment.getId())
          .setPostTitle(comment.getPost().getTitle())
          .setAuthorFullname(comment.getAuthor().getFirstName() + " " +
              comment.getAuthor().getLastName())
          .setCommentText(comment.getCommentText())
          .setBlocked(comment.getIsBlocked())
          .setDeleted(comment.getIsDeleted());
      dtos.add(dto);
    });

    return new TotalCommentDto()
        .setTotal(comments.getTotalElements())
        .setPerPage(pageable.getPageSize())
        .setOffset((int) pageable.getOffset())
        .setCommentList(dtos);
  }

  @SuppressWarnings("java:S6397")
  private String getToken(String fullName, int numeber) {
    String[] names = fullName.split("[\s]+", 0);
    for (int i = 0; i < names.length; i++) {
      if (i == numeber) {
        return StringUtils.capitalize(names[i].toLowerCase());
      }
    }
    return "";
  }

  private Authors getAuthors(CommentDescriptionDto descriptionDto) {
    return new Authors()
        .setPostAuthorFirstName(getToken(descriptionDto.getPostAuthor(), 0))
        .setPostAuthorLastName(getToken(descriptionDto.getPostAuthor(), 1))
        .setCommentAuthorFirstName(getToken(descriptionDto.getCommentAuthor(), 0))
        .setCommentAuthorLastName(getToken(descriptionDto.getCommentAuthor(), 1));
  }

  public CommentModerationDto getCommentEdit(Long id) {
    Comment comment = commentRepository.findById(id).orElse(null);
    if (comment == null) {
      return null;
    }
    return new CommentModerationDto()
        .setId(comment.getId())
        .setAuthorFullname(comment.getAuthor().getFirstName() + " " + comment.getAuthor().getLastName())
        .setCommentText(comment.getCommentText())
        .setBlocked(comment.getIsBlocked())
        .setDeleted(comment.getIsDeleted());
  }

  @Transactional
  public void postCommentEdit(CommentModerationDto commentDto) {
    Comment comment = commentRepository.findById(commentDto.getId()).orElse(null);
    if (comment == null) {
      return;
    }
    comment.setCommentText(commentDto.getCommentText());
    comment.setIsBlocked(commentDto.isBlocked());
    comment.setIsDeleted(commentDto.isDeleted());
    commentRepository.save(comment);
  }

  @Data
  @Accessors(chain = true)
  static class Authors {
    private String postAuthorFirstName;
    private String postAuthorLastName;
    private String commentAuthorFirstName;
    private String commentAuthorLastName;
  }
}
