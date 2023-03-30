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
@Schema(description = "data model of response")
public class CommonResponseDTO<T> {
  private T data;
  private String message;
  private String error;
  @Builder.Default
  private LocalDateTime timestamp = LocalDateTime.now();
}