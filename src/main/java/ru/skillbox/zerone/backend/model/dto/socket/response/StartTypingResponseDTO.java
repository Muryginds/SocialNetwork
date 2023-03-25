package ru.skillbox.zerone.backend.model.dto.socket.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StartTypingResponseDTO {
  private long authorId;
  private String author;
  @JsonProperty("dialog")
  private long dialogId;
}
