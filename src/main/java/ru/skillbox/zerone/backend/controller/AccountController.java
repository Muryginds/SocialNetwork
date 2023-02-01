package ru.skillbox.zerone.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.model.dto.request.RegisterRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.service.UserService;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountController {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<CommonResponseDTO<MessageResponseDTO>> register(@RequestBody RegisterRequestDTO request) {
    return ResponseEntity.ok(userService.registerAccount(request));
  }

  @GetMapping("/registration_complete")
  public ResponseEntity<CommonResponseDTO<MessageResponseDTO>> registrationComplete(@RequestParam String key, @RequestParam String email) {
    return userService.registrationComplete(key, email);
  }
}