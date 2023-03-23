package ru.skillbox.zerone.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.skillbox.zerone.backend.model.entity.Dialog;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.model.enumerated.ReadStatus;

import java.util.Optional;

public interface DialogRepository extends JpaRepository<Dialog, Long> {

  @Query("""
      SELECT d FROM Dialog d
      WHERE
      (d.recipient = :firstUser AND d.sender = :secondUser)
       OR (d.recipient = :secondUser AND d.sender = :firstUser)
       """)
  Optional<Dialog> findByUserDuet(User firstUser, User secondUser);

  @Query("""
      SELECT d FROM Dialog d
      WHERE d.recipient = :user OR d.sender = :user
      """)
  Page<Dialog> getPageOfDialogsByUser(User user, Pageable pageable);

  @Query("""
      SELECT count (d) FROM Dialog d, Message m
      WHERE m.dialog = d AND m.readStatus = :readStatus AND m.author != :user
        AND (d.sender = :user OR d.recipient = :user)
      """)
  long countUnreadMessagesByUser(User user, ReadStatus readStatus);
}
