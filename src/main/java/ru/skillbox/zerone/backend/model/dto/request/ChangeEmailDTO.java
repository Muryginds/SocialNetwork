package ru.skillbox.zerone.backend.model.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import ru.skillbox.zerone.backend.util.ValidationUtils;

@Data
public class ChangeEmailDTO {
  @Pattern(regexp = ValidationUtils.EMAIL_PATTERN, message = ValidationUtils.EMAIL_PATTERN_DESCRIPTION)
  private String email;
}
