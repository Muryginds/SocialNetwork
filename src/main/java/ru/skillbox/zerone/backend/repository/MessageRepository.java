package ru.skillbox.zerone.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.backend.model.entity.Dialog;
import ru.skillbox.zerone.backend.model.entity.Message;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.model.enumerated.ReadStatus;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

  Optional<Message> findFirstByDialogOrderBySentTimeDesc(Dialog dialog);

  int countByDialogAndAuthorAndReadStatus(Dialog dialog, User author, ReadStatus status);

  Page<Message> findByDialogAndMessageTextContainingIgnoreCaseAndIdIsAfter(Dialog dialog, String query, long fromMessageId, Pageable pageable);

  Page<Message> findByDialogAndIdIsAfter(Dialog dialog, long fromMessageId, Pageable pageable);
}
