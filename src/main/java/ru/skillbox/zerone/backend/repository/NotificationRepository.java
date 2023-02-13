package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.backend.model.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
