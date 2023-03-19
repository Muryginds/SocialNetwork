package ru.skillbox.zerone.backend.model.dto.socket.request;

import lombok.Data;

@Data
public class TypingData {
  private int author;
  private int dialog;
}
