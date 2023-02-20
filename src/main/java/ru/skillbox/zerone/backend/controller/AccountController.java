package ru.skillbox.zerone.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.model.dto.request.ChangeEmailDTO;
import ru.skillbox.zerone.backend.model.dto.request.ChangePasswordTokenDTO;
import ru.skillbox.zerone.backend.model.dto.request.RegisterRequestDTO;
import ru.skillbox.zerone.backend.model.dto.request.RegisterConfirmRequestDTO;
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
  public CommonResponseDTO<MessageResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
    return userService.registerAccount(request);
  }

  @PostMapping("/register/confirm")
  public CommonResponseDTO<MessageResponseDTO> registrationConfirm(@Valid @RequestBody RegisterConfirmRequestDTO request) {
    return userService.registrationConfirm(request);
  }


  @PostMapping("/password/set")
  public CommonResponseDTO<MessageResponseDTO> changePassword(@Valid @RequestBody ChangePasswordTokenDTO requestDto) {

    return userService.changePassword(requestDto);
  }


  @PostMapping("/email")
  public CommonResponseDTO<MessageResponseDTO> sendMessageForChangeEmail(@RequestBody ChangeEmailDTO requestDto) {
    return userService.sendMessageForChangeEmail(requestDto);
  }


}