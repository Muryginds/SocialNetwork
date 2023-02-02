package ru.skillbox.zerone.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.model.dto.request.RegisterRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.service.UserService;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountController {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<CommonResponseDTO<MessageResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO request) {
    return ResponseEntity.ok(userService.registerAccount(request));
  }

  @GetMapping("/registration_complete")
  public ResponseEntity<CommonResponseDTO<MessageResponseDTO>> registrationComplete(@NotBlank @RequestParam String key, @NotBlank @Email @RequestParam String email) {
    return userService.registrationComplete(key, email);
  }
}