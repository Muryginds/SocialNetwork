package ru.skillbox.zerone.backend.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skillbox.zerone.backend.model.dto.request.NotificationDTO;
import ru.skillbox.zerone.backend.model.entity.Notification;

import java.util.List;

@Mapper
public interface NotificationMapper {

  @Mapping(target = "eventType", source = "notification.type")
  @Mapping(target = "currentEntityId", expression =
      "java(ru.skillbox.zerone.backend.service.NotificationService.getCurrentEntityId(notification))")
//  @Mapping(target = "parentEntityId", expression = "java(ru.skillbox.zerone.backend.service.NotificationService.getParentEntityId(notification))")
//  @Mapping(target = "entityAuthor", expression = "java(ru.skillbox.zerone.backend.service.NotificationService.getEntityAuthorDTO(notification))")
  NotificationDTO notificationToNotification(Notification notification);

  List<NotificationDTO> notificationsToNotificationDTOs(List<Notification> notifications);
}
