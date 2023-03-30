package ru.skillbox.zerone.backend.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
@Schema(description = "Модель данных для одиночного ответа")
public class CommonResponseDTO<T> {
  private T data;

  @Schema(description = "статус", example = "ok")
  private String message;

  @Schema(description = "Сообщение в случае ошибки")
  private String error;
  @Builder.Default
  @Schema(description = "Метка времени ответа")
  private LocalDateTime timestamp = LocalDateTime.now();
}