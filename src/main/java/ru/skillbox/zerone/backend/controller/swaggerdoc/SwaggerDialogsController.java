package ru.skillbox.zerone.backend.controller.swaggerdoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import ru.skillbox.zerone.backend.model.dto.request.DialogRequestDTO;
import ru.skillbox.zerone.backend.model.dto.request.MessageRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.*;

@Tag(name = "Контроллер для работы с диалогами и сообщениями")
public interface SwaggerDialogsController {

  @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Ответ сформирован"))
  @Operation(summary = "Получить количество непрачитанных сообщений")
  CommonResponseDTO<CountDTO> getUnreadedMessages();

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Сообщение отправлено"),
      @ApiResponse(responseCode = "400", description = "Не удалось отправить сообщение", content = @Content)
  })
  @Operation(summary = "Отправка сообщения")
  CommonResponseDTO<MessageDataDTO> postMessages(long id, MessageRequestDTO messageRequestDTO);

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Список сообщений сформирован"),
      @ApiResponse(responseCode = "400", description = "Не удалось получить сообщения", content = @Content)
  })
  @Operation(summary = "Получение сообщений")
  CommonListResponseDTO<MessageDataDTO> getMessages(long id, @Min(0) int offset, @Min(0) int itemPerPage);

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Диалог создан"),
      @ApiResponse(responseCode = "400", description = "Не удалось начать диалог", content = @Content)
  })
  @Operation(summary = "Создание диалога")
  CommonResponseDTO<DialogDataDTO> postDialogs(@Valid DialogRequestDTO dialogRequestDTO);

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",description = "Список диалогов сформирован"),
      @ApiResponse(responseCode = "400", description = "Не удалось получить диалоги", content = @Content)
  })
  @Operation(summary = "Получение списка диалогов")
  CommonListResponseDTO<DialogDataDTO> getDialogs(@Min(0) int offset, @Min(0) int itemPerPage);
}
