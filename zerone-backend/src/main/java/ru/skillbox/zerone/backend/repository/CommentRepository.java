package ru.skillbox.zerone.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.zerone.backend.model.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
  Page<Comment> findCommentsByPostIdAndParentNull(long id, Pageable pageable);

  Page<Comment> findCommentsByPostIdAndParentIsNotNull(long id, Pageable pageable);
}
