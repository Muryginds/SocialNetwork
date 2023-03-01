package ru.skillbox.zerone.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.zerone.backend.model.entity.Post;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
  Page<Post> findPostsByPostTextContains(String text, Pageable pageable);

//    Set<Post> findById(int id);

    Page<Post> findPostsByAuthorId (int id, Pageable pageable);

    Page<Post> findPostsByPostTextContainsAndAuthorLastNameAndUpdateTimeBetween(String text, String author, Instant datetimeFrom, Instant datetimeTo, Pageable pageable);

  Page<Post> findPostsByPostTextContainsAndUpdateTime(String text, String author, Instant datetimeFrom, Instant datetimeTo, Pageable pageable, List<Integer> tags, int size);

//  Page<Post> findPostsByAuthorAndCurrentDate(int id, Pageable pageable);

 }
