package ru.skillbox.zerone.backend.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.UUID;

@RedisHash("WebSocketConnection")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketConnection implements Serializable {
  @Id
  private Long userId;
  private UUID sessionId;
}
