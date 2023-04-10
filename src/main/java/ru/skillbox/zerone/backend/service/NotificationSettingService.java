package ru.skillbox.zerone.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.entity.NotificationSetting;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.model.enumerated.NotificationType;
import ru.skillbox.zerone.backend.repository.NotificationSettingRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationSettingService {

  private final NotificationSettingRepository notificationSettingRepository;

  public NotificationSetting getSetting(User user) {
    Optional<NotificationSetting> optionalSetting = notificationSettingRepository.findByUser(user);
    if (optionalSetting.isPresent()) {
      return optionalSetting.get();
    } else {
      NotificationSetting setting = new NotificationSetting();
      setting.setUser(user);
      save(setting);
      return setting;
    }
  }

  public void saveNotificationTypeByUser(User user, NotificationType type, boolean enabled) {
    NotificationSetting setting = getSetting(user);
    switch (type) {
      case POST -> setting.setPostEnabled(enabled);
      case POST_COMMENT -> setting.setPostCommentEnabled(enabled);
      case COMMENT_COMMENT -> setting.setCommentCommentEnabled(enabled);
      case FRIEND_REQUEST -> setting.setFriendRequestEnabled(enabled);
      case MESSAGE -> setting.setMessagesEnabled(enabled);
      case FRIEND_BIRTHDAY -> setting.setFriendBirthdayEnabled(enabled);
    }
    save(setting);
  }

  private void save(NotificationSetting setting) {
    notificationSettingRepository.save(setting);
  }
}
