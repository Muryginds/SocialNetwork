package ru.skillbox.zerone.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.mapstruct.UserMapperImpl_;
import ru.skillbox.zerone.backend.model.dto.request.NotificationDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.entity.Notification;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.repository.NotificationRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final UserMapperImpl_ userMapperImpl;
  private final NotificationRepository notificationRepository;

  public CommonListResponseDTO<NotificationDTO> getNotifications(
      int offset, int itemPerPage) {
    User user = CurrentUserUtils.getCurrentUser();
    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
    Page<Notification> pageableNotifications =
        notificationRepository.findAllByPerson(user, pageable);
    CommonListResponseDTO.<NotificationDTO>builder()
        .total(pageableNotifications.getTotalElements())
        .perPage(itemPerPage)
        .offset(offset)
        .data(getNotificationDTOs(pageableNotifications.toList()))
        .build();
    return null;
  }

  private List<NotificationDTO> getNotificationDTOs(
      List<Notification> notifications) {
    User user = CurrentUserUtils.getCurrentUser();
    List<NotificationDTO> notificationDTOs = new ArrayList<>();
    notifications.forEach(notification -> {

    });

    return null;
  }
}
