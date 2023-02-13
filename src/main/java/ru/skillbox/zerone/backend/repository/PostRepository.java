package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.backend.model.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
