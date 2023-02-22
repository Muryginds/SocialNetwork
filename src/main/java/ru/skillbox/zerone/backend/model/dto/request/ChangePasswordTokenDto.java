package ru.skillbox.zerone.backend.model.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.skillbox.zerone.backend.util.ValidationUtils;


@Data
public class ChangePasswordTokenDto {
  private String token;
  @Size(min = 8, message = "password " + ValidationUtils.SIZE_8_PATTERN_DESCRIPTION)
  @Pattern(regexp = ValidationUtils.PASSWORD_PATTERN, message = ValidationUtils.PASSWORD_PATTERN_DESCRIPTION)
  private String password;
}
