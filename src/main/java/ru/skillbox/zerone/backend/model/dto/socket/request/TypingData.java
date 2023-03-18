package ru.skillbox.zerone.backend.model.dto.socket.request;

import lombok.Data;
import ru.skillbox.zerone.backend.model.dto.socket.Dto;

@Data
public class TypingData implements Dto {
  private int author;
  private int dialog;
}
