package ru.skillbox.zerone.backend.controller.swaggerdoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ru.skillbox.zerone.backend.model.dto.request.AuthRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;

@Tag(name = "Контроллер для авторизации пользователя")
@ApiResponse(responseCode = "403", description = "Пользователь не авторизован", content = @Content)
public interface SwaggerAuthenticationController {

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Пользователь успешно авторизован"),
      @ApiResponse(responseCode = "400", description = "Пользователь не найден",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Не верный email или пароль",
          content = @Content)})
  @Operation(summary = "Вход пользователя по его email и паролю")
  CommonResponseDTO<UserDTO> login(@Valid AuthRequestDTO requestDto);

  @Operation(summary = "Выход из приложения")
  CommonResponseDTO<MessageResponseDTO> logout(String token);
}