package ru.skillbox.zerone.backend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Модель данных для проверки статуса 'друзья'")
public class IsFriendsDTO {
  @Schema(description = "Список id пользователей, относительно которых нужно проверить статус")
  @JsonProperty("user_ids")
  private List<Long> userIds;
}
