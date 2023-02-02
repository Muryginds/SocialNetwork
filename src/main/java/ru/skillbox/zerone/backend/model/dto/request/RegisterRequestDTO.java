package ru.skillbox.zerone.backend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {

  private static final String LETTERS_PATTERN = "^[A-zА-яЁё]{2,}$";
  private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9]).{6,}$";

  @Size(min = 2, message = "user name should have at least 2 characters")
  @Pattern(regexp = LETTERS_PATTERN, message = "user name should contain only alphabetic characters")
  @NotBlank
  private String firstName;

  @Size(min = 2, message = "last name should have at least 2 characters")
  @Pattern(regexp = LETTERS_PATTERN, message = "last name should contain only alphabetic characters")
  @NotBlank
  private String lastName;

  @NotBlank
  @Size(min = 6, message = "password should have at least 6 characters")
  @Pattern(regexp = PASSWORD_PATTERN,
      message = "password must contain at least 1 uppercase, 1 lowercase, 1 special character and 1 digit")
  @JsonProperty("passwd1")
  private String password;

  @NotBlank
  @Email
  private String email;
}