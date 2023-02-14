package ru.skillbox.zerone.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.model.dto.UserDTO;
import ru.skillbox.zerone.backend.model.dto.request.AuthRequestDTO;
import ru.skillbox.zerone.backend.model.dto.request.ChangePasswordTokenDto;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.service.ChangePasswordService;
import ru.skillbox.zerone.backend.service.LoginService;



@RestController
@RequestMapping(value = "/api/v1/account/password")
@RequiredArgsConstructor
public class ChangePasswordTokenController {

  private final ChangePasswordService changePasswordService;
  @PostMapping("/set")
  public CommonResponseDTO<MessageResponseDTO> changePassword(@RequestBody ChangePasswordTokenDto requestDto) {
    return changePasswordService.changePassword(requestDto);
  }


}
