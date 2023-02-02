package ru.skillbox.zerone.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.model.dto.request.RegisterRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.service.UserService;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.util.ValidationUtils;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountController {

  private final UserService userService;

  @PostMapping("/register")
  public CommonResponseDTO<MessageResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
    return userService.registerAccount(request);
  }

  @GetMapping("/registration_complete")
  public CommonResponseDTO<MessageResponseDTO> registrationComplete(
      @NotBlank @RequestParam String key,
      @Pattern(regexp = ValidationUtils.EMAIL_PATTERN, message = "email should match form aaa@bbb.cc") @RequestParam String email) {
    return userService.registrationComplete(key, email);
  }
}