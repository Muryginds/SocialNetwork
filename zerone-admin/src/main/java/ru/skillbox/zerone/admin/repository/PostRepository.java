package ru.skillbox.zerone.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.admin.model.entity.Post;
import ru.skillbox.zerone.admin.model.entity.User;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

  List<Post> findAllByTitleAndAuthorIn(String postTitle, List<User> postAuthors);
}
