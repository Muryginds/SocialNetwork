package ru.skillbox.zerone.backend.model.dto.socket.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class SocketMessageDataDTO {
  private long id;
  @JsonProperty("time")
  private Instant time;
  @JsonProperty("author_id")
  private long authorId;
  @JsonProperty("message_text")
  private String messageText;
  @JsonProperty("read_status")
  private String readStatus;
  private boolean isSendByMe;
  @JsonProperty("dialog_id")
  private long dialogId;
}
