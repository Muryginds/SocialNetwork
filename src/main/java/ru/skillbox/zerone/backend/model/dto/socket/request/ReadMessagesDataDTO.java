package ru.skillbox.zerone.backend.model.dto.socket.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReadMessagesDataDTO {
  @JsonProperty("dialog")
  private Long dialogId;
}
