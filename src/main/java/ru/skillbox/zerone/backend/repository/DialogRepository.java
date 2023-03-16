package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.skillbox.zerone.backend.model.entity.Dialog;
import ru.skillbox.zerone.backend.model.entity.User;

import java.util.Optional;

public interface DialogRepository extends JpaRepository<Dialog, Long> {

  @Query("""
      SELECT d FROM Dialog d
      WHERE
          (d.recipient = :firstUser AND d.sender = :secondUser)
           OR (d.recipient = :secondUser AND d.sender = :firstUser)
           """)
  Optional<Dialog> findByUserDuet(User firstUser, User secondUser);
}
