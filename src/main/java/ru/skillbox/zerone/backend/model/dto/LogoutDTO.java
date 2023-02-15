package ru.skillbox.zerone.backend.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogoutDTO {
  private String additionalProp1;
  private String additionalProp2;
  private String additionalProp3;
}
