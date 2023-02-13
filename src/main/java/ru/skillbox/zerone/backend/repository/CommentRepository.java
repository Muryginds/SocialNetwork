package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.backend.model.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
