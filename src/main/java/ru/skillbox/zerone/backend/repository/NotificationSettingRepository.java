package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.backend.model.entity.NotificationSetting;
import ru.skillbox.zerone.backend.model.entity.User;

import java.util.Optional;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
  Optional<NotificationSetting> findByUser(User user);
}
