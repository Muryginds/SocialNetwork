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
  @Pattern(regexp = ValidationUtils.EMAIL_PATTERN, message = ValidationUtils.EMAIL_PATTERN_DESCRIPTION)
  private String email;
  @JsonProperty("last_name")
  @Size(min = 3, message = "last name " + ValidationUtils.SIZE_3_PATTERN_DESCRIPTION)
  @Pattern(regexp = ValidationUtils.LETTERS_PATTERN, message = "last name " + ValidationUtils.LETTERS_PATTERN_DESCRIPTION)
  private String lastName;
  @JsonProperty("message")
  @NotBlank(message = ValidationUtils.MESSAGE_NOT_EMPTY_DESCRIPTION)
  private String message;
  @JsonProperty("first_name")
  @Size(min = 3, message = "user name " + ValidationUtils.SIZE_3_PATTERN_DESCRIPTION)
  @Pattern(regexp = ValidationUtils.LETTERS_PATTERN, message = "user name " + ValidationUtils.LETTERS_PATTERN_DESCRIPTION)
  private String firstName;
}
