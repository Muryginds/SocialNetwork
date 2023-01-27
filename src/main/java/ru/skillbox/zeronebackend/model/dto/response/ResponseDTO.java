package ru.skillbox.zeronebackend.model.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseDTO <T> {

  T data;
  private String error;
  private LocalDateTime timestamp;
}