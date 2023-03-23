package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.skillbox.zerone.backend.model.entity.Dialog;

import java.util.Optional;

public interface DialogRepository extends JpaRepository<Dialog, Long> {

  @Query("""
       SELECT d FROM Dialog d, Message m
       WHERE m.id = :messageId and m.dialog = d
       """)
  Optional<Dialog> findByMessageId(Long messageId);
}
