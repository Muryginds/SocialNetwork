package ru.skillbox.zerone.backend.model.dto.socket.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StartTypingResponse {
  private int authorId;
  private String author;
  private int dialog;
}
