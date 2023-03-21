package ru.skillbox.zerone.backend.model.dto.socket.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class SocketListResponseDTO<T> {
  private String error;
  @JsonProperty("read_status")
  private String readStatus;
  private Instant timestamp;
  private T data;
}
