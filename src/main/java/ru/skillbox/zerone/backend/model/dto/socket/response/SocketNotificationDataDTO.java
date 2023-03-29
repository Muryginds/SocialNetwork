package ru.skillbox.zerone.backend.model.dto.socket.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.Builder;
import lombok.Data;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;
import ru.skillbox.zerone.backend.model.enumerated.NotificationType;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
public class SocketNotificationDataDTO {
  private long id;
  @JsonProperty("event_type")
  private NotificationType eventType;
  @JsonProperty("sent_time")
  @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant sentTime;
  @JsonProperty("entity_id")
  private long entityId;
  @JsonProperty("entity_author")
  private SocketUserDTO entityAuthor;
  @JsonProperty("parent_entity_id")
  private long parentId;
  @JsonProperty("current_entity_id")
  private long currentEntityId;
}
