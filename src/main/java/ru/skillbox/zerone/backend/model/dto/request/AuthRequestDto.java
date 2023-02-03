package ru.skillbox.zerone.backend.model.dto.request;

import lombok.Data;

@Data
public class AuthRequestDto {
  private String email;
  private String password;
}
