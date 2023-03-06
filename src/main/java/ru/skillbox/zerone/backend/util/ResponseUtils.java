package ru.skillbox.zerone.backend.util;

import lombok.experimental.UtilityClass;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;

@UtilityClass
public class ResponseUtils {
  public CommonResponseDTO<MessageResponseDTO> commonResponseWithMessage(String message) {
    return CommonResponseDTO.<MessageResponseDTO>builder()
        .data(new MessageResponseDTO(message))
        .build();
  }

  public CommonResponseDTO<MessageResponseDTO> commonResponseOk() {
    return CommonResponseDTO.<MessageResponseDTO>builder()
        .data(new MessageResponseDTO("Ok"))
        .build();
  }
}
