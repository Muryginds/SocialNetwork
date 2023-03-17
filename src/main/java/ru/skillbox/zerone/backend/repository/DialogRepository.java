package ru.skillbox.zerone.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.skillbox.zerone.backend.model.entity.Dialog;
import ru.skillbox.zerone.backend.model.entity.User;

import java.util.List;
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
      WHERE
      (d.recipient = :user AND (LOWER(d.sender.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.sender.lastName) LIKE LOWER(CONCAT('%', :query, '%'))))
       OR (d.sender = :user AND (LOWER(d.recipient.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.recipient.lastName) LIKE LOWER(CONCAT('%', :query, '%'))))
      """)
  Page<Dialog> getPageOfDialogsByUserAndQuery(User user, String query, Pageable pageable);

  @Query("""
      SELECT d FROM Dialog d
      WHERE d.recipient = :user OR d.sender = :user
      """)
  Page<Dialog> getPageOfDialogsByUser(User user, Pageable pageable);

  @Query("""
      SELECT d FROM Dialog d
      WHERE d.recipient = :user OR d.sender = :user
      """)
  List<Dialog> findAllByUser(User user);
}
