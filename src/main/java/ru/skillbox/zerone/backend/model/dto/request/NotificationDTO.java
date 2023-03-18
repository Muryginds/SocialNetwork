package ru.skillbox.zerone.backend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;
import ru.skillbox.zerone.backend.model.enumerated.NotificationType;

import java.time.LocalDateTime;

@Data
public class NotificationDTO {
  @JsonProperty("id")
  private Long id;
  @JsonProperty("sent_time")
  private LocalDateTime sentTime;
  @JsonProperty("event_type")
  private NotificationType eventType;
  @JsonProperty("entity_author")
  private UserDTO entityAuthor;
  @JsonProperty("current_entity_id")
  private Long currentEntityId;
  @JsonProperty("entity_id")
  private Long entityId;
  @JsonProperty("parent_entity_id")
  private Long parentEntityId;
}
