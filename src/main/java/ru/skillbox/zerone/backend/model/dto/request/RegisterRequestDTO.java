package ru.skillbox.zerone.backend.model.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterRequestDTO {
  private String firstName;
  private String lastName;
  @JsonProperty("passwd1")
  private String password;
  private String email;
}