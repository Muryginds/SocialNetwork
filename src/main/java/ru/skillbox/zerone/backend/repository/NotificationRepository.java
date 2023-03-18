package ru.skillbox.zerone.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.backend.model.entity.Notification;
import ru.skillbox.zerone.backend.model.entity.User;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  Page<Notification> findAllByPerson(User person, Pageable pageable);
}
