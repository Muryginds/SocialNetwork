package ru.skillbox.zerone.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.zerone.backend.model.entity.Post;
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
  Page<Post> findPostsByPostTextContains(String text, Pageable pageable);
}
