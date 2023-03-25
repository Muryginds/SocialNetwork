package ru.skillbox.zerone.backend.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.UUID;

@RedisHash
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketConnection implements Serializable {
  @Id
  private UUID sessionId;
  @Indexed
  private String userId;
}
