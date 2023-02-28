package ru.skillbox.zerone.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.service.FriendsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friends")
public class FriendsController {
  private final FriendsService friendsService;

  @PostMapping("/{id}")
  public CommonResponseDTO<Object> addFriend(@PathVariable Long id) {
    return friendsService.addFriend(id);
  }

  @DeleteMapping("/{id}")
  public CommonResponseDTO<Object> removeFriend(@PathVariable Long id) {
    return friendsService.removeFriend(id);
  }
}
