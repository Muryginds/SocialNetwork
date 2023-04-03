package ru.skillbox.zerone.backend.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Обобщенная модель данных для ответа, содержащего список сущностей")
public class CommonListResponseDTO<T> {
  @Schema(description = "Элементов всего")
  private long total;
  @Schema(description = "Элементов на странице")
  private int perPage;
  @Schema(description = "Игнорировать первые N элементов")
  private int offset;
  private List<T> data;
  @Schema(description = "Сообщение в случае ошибки")
  private String error;
  @Builder.Default
  @Schema(description = "Метка времени ответа")
  private LocalDateTime timestamp = LocalDateTime.now();
}
