package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.zerone.backend.model.entity.File;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
  void deleteByPublicId(String publicId);
  List<File> findAllByIsStartAvatar(boolean isStartAvatar);
}