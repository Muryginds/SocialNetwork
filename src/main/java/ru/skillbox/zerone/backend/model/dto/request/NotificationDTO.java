package ru.skillbox.zerone.backend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;
import ru.skillbox.zerone.backend.model.enumerated.NotificationType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
  @JsonProperty("id")
  private Long id;
  @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
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
