package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.zerone.backend.model.entity.PostFile;

@Repository
public interface PostFileRepository extends JpaRepository<PostFile, Long> {
  PostFile findByPath(String path);
}
