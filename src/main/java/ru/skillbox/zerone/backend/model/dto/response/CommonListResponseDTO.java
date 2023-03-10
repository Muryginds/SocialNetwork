package ru.skillbox.zerone.backend.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonListResponseDTO<T> {
  private int total;
  private int perPage;
  private int offset;
  private List <T> data;
  private String error;
  private LocalDateTime timestamp;
}