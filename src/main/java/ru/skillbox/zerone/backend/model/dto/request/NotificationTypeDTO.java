package ru.skillbox.zerone.backend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NotificationTypeDTO {
  @JsonProperty("notification_type")
  private String type;
  private Boolean enable;
}
