package ru.skillbox.zerone.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.zerone.backend.model.entity.Comment;
import ru.skillbox.zerone.backend.model.entity.PostComment;
@Repository
public interface CommentRepository extends JpaRepository<PostComment, Integer> {
    Page<PostComment> findPostCommentsByPostIdAndParentIsNullOrderByTime(int post, Pageable pageable);
}
