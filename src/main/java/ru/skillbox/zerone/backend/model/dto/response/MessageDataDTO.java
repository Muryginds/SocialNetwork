package ru.skillbox.zerone.backend.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Модель данных представления сообщения")
public class MessageDataDTO {
  @JsonProperty("message_text")
  private String messageText;
  @Schema(description = "Сообщение отправлено текущим пользователем")
  private boolean sendByMe;
  @Schema(description = "Статус сообщения")
  @JsonProperty("read_status")
  private String readStatus;
  @JsonProperty("dialog_id")
  private long dialogId;
  @Schema(description = "Идентификатор сообщения в системе")
  private long id;
  @Schema(description = "Время отправки")
  private LocalDateTime time;
  @Schema(description = "Автор сообщения")
  @JsonProperty("author_id")
  private long authorId;
}