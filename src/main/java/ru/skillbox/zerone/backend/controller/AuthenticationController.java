package ru.skillbox.zerone.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.model.dto.UserDto;
import ru.skillbox.zerone.backend.model.dto.request.AuthRequestDto;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.service.LoginService;


@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final LoginService loginService;
  @PostMapping("/login")
  public CommonResponseDTO<UserDto> login(@RequestBody AuthRequestDto requestDto) {
    return loginService.login(requestDto);
  }

  @GetMapping("/logout")
  public ResponseEntity<Object> logout() {
    return ResponseEntity.ok().build();
  }
}