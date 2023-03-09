package ru.skillbox.zerone.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.zerone.backend.model.entity.Post;
import java.time.LocalDateTime;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
  Page<Post> findPostsByPostTextContains(String text, Pageable pageable);

//    Set<Post> findById(int id);

    Page<Post> findPostsByAuthorId (long id, Pageable pageable);

    Page<Post> findPostsByPostTextContainsAndAuthorLastNameAndUpdateTimeBetween(String text, String author, LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable);


//  Set<Post> findPostsByTag(String tag);

//  Page<Post> findPostsByAuthorAndCurrentDate(int id, Pageable pageable);

 }
