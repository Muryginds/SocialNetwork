package ru.skillbox.zerone.backend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.skillbox.zerone.backend.util.ValidationUtils;

@Data
public class SupportRequestDTO {
  @JsonProperty("e_mail")
  @Pattern(regexp = ValidationUtils.EMAIL_PATTERN, message = "email must match form aaa@bbb.cc")
  private String email;
  @JsonProperty("last_name")
  @Size(min = 2, message = "last name should have at least 2 characters")
  @Pattern(regexp = ValidationUtils.LETTERS_PATTERN, message = "last name must contain only alphabetic characters")
  private String lastName;
  @JsonProperty("message")
  @NotBlank(message = "message must be not empty")
  private String message;
  @JsonProperty("first_name")
  @Size(min = 2, message = "user name must have at least 2 characters")
  @Pattern(regexp = ValidationUtils.LETTERS_PATTERN, message = "user name must contain only alphabetic characters")
  private String firstName;
}
