package ru.skillbox.zerone.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.model.dto.request.AuthRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;
import ru.skillbox.zerone.backend.service.LoginService;

@Validated
@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  private final LoginService loginService;

  @Operation(summary = "login by email and password")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found user normally"),
      @ApiResponse(responseCode = "400", description = "User not found",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Bad credentials",
          content = @Content)})
  @PostMapping("/login")
  public CommonResponseDTO<UserDTO> login(@Valid @RequestBody AuthRequestDTO requestDto) {
    return loginService.login(requestDto);
  }

  @Operation(summary = "exit the application")
  @GetMapping("/logout")
  public CommonResponseDTO<MessageResponseDTO> logout(
      @RequestHeader(name = "Authorization") String token) {
    return loginService.logout(token);
  }
}