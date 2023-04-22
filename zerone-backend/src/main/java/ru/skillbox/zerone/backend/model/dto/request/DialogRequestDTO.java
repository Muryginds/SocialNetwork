package ru.skillbox.zerone.backend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Модель данных для создания диалогов")
public class DialogRequestDTO {
  @Schema(description = "Список идентификаторов пользователей")
  @JsonProperty("users_ids")
  private List<Long> usersIds;
}
