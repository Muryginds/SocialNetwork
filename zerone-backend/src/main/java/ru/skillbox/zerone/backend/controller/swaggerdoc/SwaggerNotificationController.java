package ru.skillbox.zerone.backend.controller.swaggerdoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.skillbox.zerone.backend.model.dto.request.NotificationDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;

@Tag(name = "Контроллер для управления уведомлениями")
@ApiResponse(responseCode = "403", description = "Пользователь не авторизован", content = @Content)
public interface SwaggerNotificationController {
  @Operation(summary = "Получение уведомлений")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Список уведомлений успешно получен"),
      @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = @Content)
  })
  @GetMapping
  CommonListResponseDTO<NotificationDTO> getNotifications(
      @RequestParam(name = "offset", defaultValue = "0") int offset,
      @RequestParam(name = "itemPerPage", defaultValue = "20") int itemPerPage
  );

  @Operation(summary = "Отметить уведомления как прочитанные")
  @ApiResponse(responseCode = "200", description = "Уведомления успешно отмечены как прочитанные")
  @ApiResponse(responseCode = "400", description = "Некорректные данные для отметки уведомлений как прочитанные", content = @Content)
  @PutMapping
  CommonListResponseDTO<NotificationDTO> putNotifications(
      @Parameter(description = "Смещение для постраничного вывода") @RequestParam(name = "offset") int offset,
      @Parameter(description = "Количество элементов на странице") @RequestParam(name = "itemPerPage") int itemPerPage,
      @Parameter(description = "Идентификатор пользователя") @RequestParam(name = "id") int id,
      @Parameter(description = "Флаг, указывающий, отмечать ли все уведомления как прочитанные") @RequestParam(name = "all") boolean all
  );
}
