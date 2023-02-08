package ru.skillbox.zerone.backend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.skillbox.zerone.backend.util.ValidationUtils;

@Data
public class RegisterRequestDTO {
  @Size(min = 3, message = "user name " + ValidationUtils.SIZE_3_PATTERN_DESCRIPTION)
  @Pattern(regexp = ValidationUtils.LETTERS_PATTERN, message = "user name " + ValidationUtils.LETTERS_PATTERN_DESCRIPTION)
  private String firstName;

  @Size(min = 3, message = "last name " + ValidationUtils.SIZE_3_PATTERN_DESCRIPTION)
  @Pattern(regexp = ValidationUtils.LETTERS_PATTERN, message = "last name " + ValidationUtils.LETTERS_PATTERN_DESCRIPTION)
  private String lastName;

  @Size(min = 8, message = "password " + ValidationUtils.SIZE_8_PATTERN_DESCRIPTION)
  @Pattern(regexp = ValidationUtils.PASSWORD_PATTERN, message = ValidationUtils.PASSWORD_PATTERN_DESCRIPTION)
  @JsonProperty("passwd1")
  private String password;

  @Pattern(regexp = ValidationUtils.EMAIL_PATTERN, message = ValidationUtils.EMAIL_PATTERN_DESCRIPTION)
  private String email;
}