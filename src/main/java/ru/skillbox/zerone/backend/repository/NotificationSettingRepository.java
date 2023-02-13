package ru.skillbox.zerone.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.zerone.backend.model.entity.NotificationSetting;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
}
