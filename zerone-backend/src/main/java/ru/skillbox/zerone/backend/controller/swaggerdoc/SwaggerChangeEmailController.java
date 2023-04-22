package ru.skillbox.zerone.backend.controller.swaggerdoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.RequestParam;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;

public interface SwaggerChangeEmailController {
  @Operation(summary = "Подтверждение изменения email")
  @ApiResponse(responseCode = "200", description = "Email успешно изменен")
  @ApiResponse(responseCode = "400", description = "Некорректные данные для изменения email")
  CommonResponseDTO<MessageResponseDTO> changeEmailConfirm(
      @RequestParam String userId,
      @RequestParam String token
  );
}
