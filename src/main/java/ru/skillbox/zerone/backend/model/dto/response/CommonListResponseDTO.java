package ru.skillbox.zerone.backend.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonListResponseDTO<T> {
  private long total;
  private int perPage;
  private int offset;
  private List<T> data;
  private String error;
  @Builder.Default
  private LocalDateTime timestamp = LocalDateTime.now();
}