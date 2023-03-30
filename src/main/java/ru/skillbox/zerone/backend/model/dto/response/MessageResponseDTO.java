package ru.skillbox.zerone.backend.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "data model of message_response")
public class MessageResponseDTO {
  private String message;
}