package ru.skillbox.zerone.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.model.dto.UserDTO;
import ru.skillbox.zerone.backend.model.dto.request.AuthRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.service.LoginService;


@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final LoginService loginService;
  @PostMapping("/login")
  public CommonResponseDTO<UserDTO> login(@RequestBody AuthRequestDTO requestDto) {
    return loginService.login(requestDto);
  }

  @GetMapping("/logout")
  public CommonResponseDTO<MessageResponseDTO> logout() {
    return loginService.logout();
  }
}