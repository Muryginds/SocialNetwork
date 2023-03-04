package ru.skillbox.zerone.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.service.UserService;

@RestController
@Validated
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersController {
  private final UserService userService;


  @GetMapping("/me")
  public CommonResponseDTO<UserDTO> getCurrentUser() {
      return userService.getCurrentUser();
  }
  @GetMapping("/{id}")
  public CommonResponseDTO<UserDTO> getById(@PathVariable @Min(1) Long id) {
  return userService.getById(id);
}
  @PutMapping("/me")
  public ResponseEntity<Object> editUserSettings(@Valid @RequestBody UserDTO updateUser) {
    userService.editUserSettings(updateUser);
    return ResponseEntity.ok().build();
  }
}