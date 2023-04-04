package ru.skillbox.zerone.backend.controller.swaggerdoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import ru.skillbox.zerone.backend.model.dto.request.IsFriendsDTO;
import ru.skillbox.zerone.backend.model.dto.response.*;

@Tag(name = "Контроллер для работы с друзьями")
@ApiResponse(responseCode = "403", description = "Пользователь не авторизован")
public interface SwaggerFriendsController {

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Добавление прошло успешно"),
      @ApiResponse(responseCode = "400", description = "Не получается добавить в друзья", content = @Content)
  })
  @Operation(summary = "Добавление в друзья")
  CommonResponseDTO<MessageResponseDTO> addFriend(long id);

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Удаление прошло успешно"),
      @ApiResponse(responseCode = "400", description = "Не получается удалить из друзей", content = @Content)
  })
  @Operation(summary = "Удаление из друзей")
  CommonResponseDTO<MessageResponseDTO> removeFriend(long id);

  @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Список друзей сформирован"))
  @Operation(summary = "Получить список друзей")
  CommonListResponseDTO<UserDTO> getFriendList(String name, @Min(0) int offset, @Min(0) int itemPerPage);

  @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Список запросов сформирован"))
  @Operation(summary = "Получить список запросов в друзья")
  CommonListResponseDTO<UserDTO> getFriendRequestList(String name, @Min(0) int offset, @Min(0) int itemPerPage);

  @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Список ответов сформирован"))
  @Operation(summary = "Являются ли пользователи друзьями",
      description = "Получить информацию является ли пользователь другом указанных пользователей")
  CommonListResponseDTO<StatusFriendDTO> checkIsFriend(@Valid IsFriendsDTO isFriendsDTO);

  @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Список рекоммендаций сформирован"))
  @Operation(summary = "Получить список рекомендаций", description = "Получить список персональных рекомендация для пользователя")
  CommonListResponseDTO<UserDTO> getRecommendations(@Min(0) int offset, @Min(0) int itemPerPage);
}
