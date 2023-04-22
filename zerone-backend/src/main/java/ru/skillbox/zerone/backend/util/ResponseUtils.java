package ru.skillbox.zerone.backend.util;

import lombok.experimental.UtilityClass;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;

@UtilityClass
public class ResponseUtils {
  public CommonResponseDTO<MessageResponseDTO> commonResponseWithDataMessage(String message) {
    return CommonResponseDTO.<MessageResponseDTO>builder()
        .data(new MessageResponseDTO(message))
        .build();
  }

  public <T> CommonResponseDTO<T> commonResponseWithData(T t) {
    return CommonResponseDTO.<T>builder()
        .data(t)
        .build();
  }

  public CommonResponseDTO<MessageResponseDTO> commonResponseDataOk() {
    return CommonResponseDTO.<MessageResponseDTO>builder()
        .data(new MessageResponseDTO("OK"))
        .build();
  }

  public CommonResponseDTO<Object> commonResponseWithError(String message) {
    return CommonResponseDTO.builder()
        .error(message)
        .build();
  }
}
