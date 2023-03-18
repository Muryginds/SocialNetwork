package ru.skillbox.zerone.backend.model.dto.socket.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;
import ru.skillbox.zerone.backend.model.dto.socket.Dto;
import ru.skillbox.zerone.backend.model.enumerated.NotificationType;

import java.time.Instant;

public class SocketNotificationData implements Dto {
  private int id;
  @JsonProperty("event_type")
  private NotificationType eventType;
  @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  @JsonProperty("sent_time")
  private Instant sentTime;
  @JsonProperty("entity_id")
  private int entityId;
  @JsonProperty("entity_author")
  private UserDTO entityAuthor;
  @JsonProperty("parent_entity_id")
  private int parentId;
  @JsonProperty("current_entity_id")
  private int currentEntityId;

}
