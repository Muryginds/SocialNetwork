package ru.skillbox.zerone.admin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceDto {
  private Long id;
  private String email;
  private String fullName;
  private String message;
  @DateTimeFormat(pattern = "dd.MM.yyyy hh:mm:ss")
  private String createdAt;
  private String answer;
}
