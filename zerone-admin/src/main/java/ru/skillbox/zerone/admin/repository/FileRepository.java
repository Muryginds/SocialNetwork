package ru.skillbox.zerone.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.zerone.admin.model.entity.File;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
  void deleteAllByIsStartAvatar(boolean isStartAvatar);

  List<File> findAllByIsStartAvatar(boolean isStartAvatar);
}