package ru.skillbox.zerone.backend.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.zerone.backend.util.ValidationUtils;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель данных для объекта \"auth_request\"")
public class AuthRequestDTO {
  @Pattern(regexp = ValidationUtils.EMAIL_PATTERN, message = ValidationUtils.EMAIL_PATTERN_DESCRIPTION)
  @Schema(description = "Пользовательский email", example = "Dimatch86@mail.ru")
  private String email;
  @Schema(description = "Пользовательский пароль, должен содержать не менее 8 символов из латинских букв" +
      "(одна из которых должна быть заглавной), цифр и знаков. ",
      example = "Leiva853$")
  private String password;
}
