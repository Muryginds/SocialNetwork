package ru.skillbox.zerone.backend.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDataDTO {
  @JsonProperty("message_text")
  private String messageText;
  private boolean sendByMe;
  @JsonProperty("read_status")
  private String readStatus;
  @JsonProperty("dialog_id")
  private long dialogId;
  private long id;
  private LocalDateTime time;
  @JsonProperty("author_id")
  private long authorId;
}