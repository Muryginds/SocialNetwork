package ru.skillbox.zerone.backend.model.dto.socket.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StopTypingResponseDTO {
  @JsonProperty("author")
  private long authorId;
  @JsonProperty("dialog")
  private long dialogId;
}
