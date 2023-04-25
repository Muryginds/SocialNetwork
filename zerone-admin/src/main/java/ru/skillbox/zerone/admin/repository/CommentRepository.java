package ru.skillbox.zerone.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.admin.model.entity.Comment;
import ru.skillbox.zerone.admin.model.entity.Post;
import ru.skillbox.zerone.admin.model.entity.User;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  Page<Comment> findAllByPostIn(List<Post> posts, Pageable pageable);

  Page<Comment> findAllByPostInAndCommentTextContains(
      List<Post> posts, String fragment, Pageable pageable);

  Page<Comment> findAllByPostInAndAuthorIn(List<Post> posts, List<User> authors, Pageable pageable);

  Page<Comment> findAllByPostInAndAuthorInAndCommentTextContains(
      List<Post> posts, List<User> authors, String fragment, Pageable pageable);
}
