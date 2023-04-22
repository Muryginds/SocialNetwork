package ru.skillbox.zerone.backend.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@Schema(description = "Модель данных с идентификатором и названием")
public class BasicEntityDTO {
  private Long id;
  @JsonProperty("title")
  private String name;
}
