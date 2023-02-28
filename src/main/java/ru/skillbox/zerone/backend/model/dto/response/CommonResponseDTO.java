package ru.skillbox.zerone.backend.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class CommonResponseDTO<T> {
  private T data;
  private String message;
  private String error;
  @Builder.Default
  private LocalDateTime timestamp = LocalDateTime.now();
}