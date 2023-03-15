package ru.skillbox.zerone.backend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NotificationTypeDTO {
  @JsonProperty("notification_type")
  String type;
  Boolean enable;
}
