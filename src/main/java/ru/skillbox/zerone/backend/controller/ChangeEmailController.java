package ru.skillbox.zerone.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.service.UserService;


@RestController
@RequiredArgsConstructor
public class ChangeEmailController {

  private final UserService userService;

  @PostMapping(value = "/registration/complete", params = {"userId", "token"})
  public CommonResponseDTO<MessageResponseDTO> changeEmailConfirm(@RequestParam String userId, @RequestParam String token) {

    return userService.changeEmailConfirm(userId, token);


  }

}
