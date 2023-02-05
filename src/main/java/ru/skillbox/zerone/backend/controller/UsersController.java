package ru.skillbox.zerone.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.zerone.backend.model.dto.UserDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersController {

  private final UserService userService;

  @GetMapping("/me")
  public CommonResponseDTO<UserDTO> getCurrentUser() {
      return userService.getCurrentUser();
  }
}