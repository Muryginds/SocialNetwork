package ru.skillbox.zerone.backend.controller.swaggerdoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;

@Tag(name = "Контроллер для управления пользователями")
public interface SwaggerUsersController {

  @Operation(summary = "Получить информацию о текущем пользователе")
  @ApiResponse(responseCode = "200", description = "Успешно получена информация о текущем пользователе")
  CommonResponseDTO<UserDTO> getCurrentUser();

  @Operation(summary = "Получить информацию о пользователе по id")
  @ApiResponse(responseCode = "200", description = "Успешно получена информация о пользователе")
  CommonResponseDTO<UserDTO> getById(@PathVariable long id);

  @Operation(summary = "Редактировать настройки пользователя")
  @ApiResponse(responseCode = "200", description = "Настройки пользователя успешно изменены")
  UserDTO editUserSettings(@RequestBody UserDTO updateUser);

  @Operation(summary = "Поиск пользователей по заданным параметрам")
  @ApiResponse(responseCode = "200", description = "Успешный поиск пользователей")
  @SuppressWarnings("java:S107")
  CommonListResponseDTO<UserDTO> getUser(@RequestParam(name = "first_name", required = false) String name,
                                         @RequestParam(name = "last_name", required = false) String lastName,
                                         @RequestParam(name = "country", required = false) String country,
                                         @RequestParam(name = "city", required = false) String city,
                                         @RequestParam(name = "age_from", required = false) Integer ageFrom,
                                         @RequestParam(name = "age_to", required = false) Integer ageTo,
                                         @RequestParam(name = "offset", defaultValue = "0") int offset,
                                         @RequestParam(name = "itemPerPage", defaultValue = "10") int itemPerPage);

  @Operation(summary = "Заблокировать пользователя")
  @ApiResponse(responseCode = "200", description = "Пользователь успешно заблокирован")
  CommonResponseDTO<MessageResponseDTO> blockUser(@PathVariable long id);

  @Operation(summary = "Разблокировать пользователя")
  @ApiResponse(responseCode = "200", description = "Пользователь успешно разблокирован")
  CommonResponseDTO<MessageResponseDTO> unblockUser(@PathVariable long id);
}
