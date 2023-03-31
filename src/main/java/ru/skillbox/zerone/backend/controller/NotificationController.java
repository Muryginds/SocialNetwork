package ru.skillbox.zerone.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.model.dto.request.NotificationDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.service.NotificationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping
  public CommonListResponseDTO<NotificationDTO> getNotifications(
      @RequestParam(name = "offset", defaultValue = "0") int offset,
      @RequestParam(name = "itemPerPage", defaultValue = "20") int itemPerPage)
  {
    return notificationService.getNotifications(offset, itemPerPage);
  }

   @PutMapping
  public CommonListResponseDTO<NotificationDTO> putNotifications(
      @RequestParam(name = "offset") int offset,
      @RequestParam(name = "itemPerPage") int itemPerPage,
      @RequestParam(name = "id") int id,
      @RequestParam(name = "all") boolean all
  ) {
    return notificationService.putNotifications();
  }

}
