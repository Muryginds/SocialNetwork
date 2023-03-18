package ru.skillbox.zerone.backend.model.dto.socket.response;

import lombok.Builder;
import lombok.Data;
import ru.skillbox.zerone.backend.model.dto.socket.Dto;

@Data
@Builder
public class StartTypingResponse implements Dto {
  private int authorId;
  private String author;
  private int dialog;
}
