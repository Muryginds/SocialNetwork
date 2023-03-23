package ru.skillbox.zerone.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.backend.model.entity.Notification;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.model.enumerated.ReadStatus;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  Page<Notification> findAllByPersonAndStatus(User person, ReadStatus status, Pageable pageable);

}
