package ru.skillbox.zerone.backend.controller.swaggerdoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ru.skillbox.zerone.backend.model.dto.request.*;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;

@Tag(name = "Контроллер для управления аккаунтом")
public interface SwaggerAccountController {

  @Operation(summary = "Зарегистрировать новый аккаунт")
  @ApiResponse(responseCode = "200", description = "Аккаунт успешно зарегистрирован")
  @ApiResponse(responseCode = "400", description = "Некорректный запрос. Проверьте параметры запроса.", content = @Content)
  CommonResponseDTO<MessageResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request);

  @Operation(summary = "Подтвердить регистрацию аккаунта")
  @ApiResponse(responseCode = "200", description = "Регистрация аккаунта успешно подтверждена")
  @ApiResponse(responseCode = "400", description = "Некорректный запрос. Проверьте параметры запроса.", content = @Content)
  @ApiResponse(responseCode = "403", description = "Пользователь не авторизован", content = @Content)
  CommonResponseDTO<MessageResponseDTO> registrationConfirm(@Valid @RequestBody RegisterConfirmRequestDTO request);

  @Operation(summary = "Изменить пароль аккаунта")
  @ApiResponse(responseCode = "200", description = "Пароль успешно изменен")
  @ApiResponse(responseCode = "400", description = "Некорректный запрос. Проверьте параметры запроса.", content = @Content)
  @ApiResponse(responseCode = "403", description = "Пользователь не авторизован", content = @Content)
  CommonResponseDTO<MessageResponseDTO> changePassword(@Valid @RequestBody ChangePasswordDTO requestDto);

  @Operation(summary = "Изменить email аккаунта")
  @ApiResponse(responseCode = "200", description = "Email успешно изменен")
  @ApiResponse(responseCode = "400", description = "Некорректный запрос. Проверьте параметры запроса.", content = @Content)
  @ApiResponse(responseCode = "403", description = "Пользователь не авторизован", content = @Content)
  CommonResponseDTO<MessageResponseDTO> sendMessageForChangeEmail(@RequestBody ChangeEmailDTO requestDto);

  @Operation(summary = "Изменить настройки уведомлений")
  @ApiResponse(responseCode = "200", description = "Настройки уведомлений успешно изменены")
  @ApiResponse(responseCode = "400", description = "Некорректный запрос. Проверьте параметры запроса.", content = @Content)
  @ApiResponse(responseCode = "403", description = "Пользователь не авторизован", content = @Content)
  CommonResponseDTO<MessageResponseDTO> setNotificationType(@RequestBody NotificationSettingDTO typeDTO);
}