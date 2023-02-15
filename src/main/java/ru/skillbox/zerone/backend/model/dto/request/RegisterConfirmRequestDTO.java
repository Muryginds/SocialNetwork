package ru.skillbox.zerone.backend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterConfirmRequestDTO {
  @JsonProperty("userId")
  private String email;
  @JsonProperty("token")
  private String confirmationKey;
}