package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.backend.model.entity.PostFile;

public interface PostFileRepository extends JpaRepository<PostFile, Long> {
}
