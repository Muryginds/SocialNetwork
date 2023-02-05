package ru.skillbox.zerone.backend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.skillbox.zerone.backend.util.ValidationUtils;

@Data
public class RegisterRequestDTO {
  @Size(min = 2, message = "user name must have at least 2 characters")
  @Pattern(regexp = ValidationUtils.LETTERS_PATTERN, message = "user name must contain only alphabetic characters")
  private String firstName;

  @Size(min = 2, message = "last name should have at least 2 characters")
  @Pattern(regexp = ValidationUtils.LETTERS_PATTERN, message = "last name must contain only alphabetic characters")
  private String lastName;

  @Size(min = 6, message = "password must have at least 6 characters")
  @Pattern(regexp = ValidationUtils.PASSWORD_PATTERN,
      message = "password must contain at least 1 uppercase, 1 lowercase, 1 special character and 1 digit")
  @JsonProperty("passwd1")
  private String password;

  @Pattern(regexp = ValidationUtils.EMAIL_PATTERN, message = "email must match form aaa@bbb.cc")
  private String email;
}