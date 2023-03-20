package ru.skillbox.zerone.backend.model.dto.socket.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;
import ru.skillbox.zerone.backend.model.enumerated.NotificationType;

import java.time.Instant;

public class SocketNotificationDataDTO {
  private long id;
  @JsonProperty("event_type")
  private NotificationType eventType;
//  @JsonSerialize(using = InstantSerializer.class)
//  @JsonDeserialize(using = InstantDeserializer.class)
  @JsonProperty("sent_time")
  private Instant sentTime;
  @JsonProperty("entity_id")
  private long entityId;
  @JsonProperty("entity_author")
  private UserDTO entityAuthor;
  @JsonProperty("parent_entity_id")
  private long parentId;
  @JsonProperty("current_entity_id")
  private long currentEntityId;
}
