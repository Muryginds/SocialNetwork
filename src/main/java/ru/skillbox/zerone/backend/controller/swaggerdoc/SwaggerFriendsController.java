package ru.skillbox.zerone.backend.controller.swaggerdoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ru.skillbox.zerone.backend.model.dto.request.IsFriendsDTO;
import ru.skillbox.zerone.backend.model.dto.response.*;

@Tag(name = "Контроллер для работы с друзьями")
public interface SwaggerFriendsController {

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Добавление прошло успешно"),
      @ApiResponse(responseCode = "400", description = "Не получается добавить")
  })
  @Operation(summary = "Добавление в друзья")
  CommonResponseDTO<MessageResponseDTO> addFriend(long id);

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Удаление прошло успешно"),
      @ApiResponse(responseCode = "400", description = "Не получается удалить")
  })
  @Operation(summary = "Удаление из друзей")
  CommonResponseDTO<MessageResponseDTO> removeFriend(long id);

  @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Список успешно сформирован"))
  @Operation(summary = "Получить список друзей")
  CommonListResponseDTO<UserDTO> getFriendList(String name, int offset, int itemPerPage);

  @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Список успешно сформирован"))
  @Operation(summary = "Получить список запросов в друзья")
  CommonListResponseDTO<UserDTO> getFriendRequestList(String name, int offset, int itemPerPage);

  @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Список с результатами успешно сформирован"))
  @Operation(summary = "Являются ли пользователи друзьями",
      description = "Получить информацию является ли пользователь другом указанных пользователей")
  CommonListResponseDTO<StatusFriendDTO> checkIsFriend(@Valid IsFriendsDTO isFriendsDTO);

  @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Список успешно сформирован"))
  @Operation(summary = "Получить список рекомендаций", description = "Получить список персональных рекомендация для пользователя")
  CommonListResponseDTO<UserDTO> getRecommendations(int offset, int itemPerPage);
}
