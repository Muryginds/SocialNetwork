package ru.skillbox.zerone.backend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель данных для отправки текста")
public class MessageRequestDTO {
  @JsonProperty("message_text")
  private String messageText;
}