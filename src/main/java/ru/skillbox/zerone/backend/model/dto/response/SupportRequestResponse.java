package ru.skillbox.zerone.backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class SupportRequestResponse extends BaseResponse {
  private String message;
}
