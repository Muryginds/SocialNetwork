package ru.skillbox.zerone.backend.controller.swaggerdoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ru.skillbox.zerone.backend.model.dto.request.SupportRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;

@Tag(name = "Контроллер для обращений к поддержке")
public interface SwaggerSupportRequestController {
  @Operation(summary = "Создание заявки на поддержку")
  @ApiResponse(responseCode = "201", description = "Заявка успешно создана")
  @ApiResponse(responseCode = "400", description = "Некорректные данные для создания заявки", content = @Content)
  CommonResponseDTO<MessageResponseDTO> registerSupportRequest(
      @RequestBody @Valid SupportRequestDTO supportRequestDTO);
}
