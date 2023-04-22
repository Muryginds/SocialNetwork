package ru.skillbox.zerone.backend.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Модель данных представления диалога")
public class DialogDataDTO {
  @Schema(description = "Количество непрочитанных сообщений")
  @JsonProperty("unread_count")
  private int unreadCount;
  @Schema(description = "Последнее сообщение в диалоге")
  @JsonProperty("last_message")
  private MessageDataDTO lastMessage;
  @Schema(description = "Идентификатор диалога")
  private long id;
  @Schema(description = "Идентификатор собеседника")
  @JsonProperty("recipient_id")
  private UserDTO companion;
}
