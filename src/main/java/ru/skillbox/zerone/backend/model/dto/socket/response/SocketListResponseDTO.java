package ru.skillbox.zerone.backend.model.dto.socket.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class SocketListResponseDTO<T> {
  private String error;
  @Builder.Default
  @JsonSerialize(using = InstantSerializer.class)
  private Instant timestamp = Instant.now();
  private T data;
}
