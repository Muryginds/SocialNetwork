package ru.skillbox.zerone.backend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupportRequestDTO {
  @JsonProperty("e_mail")
  @Email(message = "email must be as 'aaaa@bbbb.ccc")
  private String email;
  @JsonProperty("last_name")
  @NotBlank(message = "last_name must be not empty")
  private String lastName;
  @JsonProperty("message")
  @NotBlank(message = "message must be not empty")
  private String message;
  @JsonProperty("first_name")
  @NotBlank(message = "first_name must be not empty")
  private String firstName;
}
