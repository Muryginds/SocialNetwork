package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.backend.model.entity.PostToTag;

public interface PostToTagRepository extends JpaRepository<PostToTag, Long> {
}
