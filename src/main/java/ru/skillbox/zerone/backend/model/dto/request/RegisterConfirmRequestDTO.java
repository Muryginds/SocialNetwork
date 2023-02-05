package ru.skillbox.zerone.backend.model.dto.request;

import lombok.Data;

@Data
public class RegisterConfirmRequestDTO {
  private String userId;
  private String token;
}