package ru.skillbox.zerone.backend.model.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import ru.skillbox.zerone.backend.util.ValidationUtils;

@Data
@Builder
public class AuthRequestDTO {
  @Pattern(regexp = ValidationUtils.EMAIL_PATTERN, message = ValidationUtils.EMAIL_PATTERN_DESCRIPTION)
  private String email;
  private String password;
}
