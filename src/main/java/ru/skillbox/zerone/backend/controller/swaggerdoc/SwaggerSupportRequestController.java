package ru.skillbox.zerone.backend.controller.swaggerdoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import ru.skillbox.zerone.backend.model.dto.request.SupportRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;

public interface SwaggerSupportRequestController {
  @Operation(summary = "Создание заявки на поддержку")
  @ApiResponse(responseCode = "201", description = "Заявка успешно создана")
  @ApiResponse(responseCode = "400", description = "Некорректные данные для создания заявки")
  CommonResponseDTO<MessageResponseDTO> registerSupportRequest(
      @RequestBody @Valid SupportRequestDTO supportRequestDTO);
}
