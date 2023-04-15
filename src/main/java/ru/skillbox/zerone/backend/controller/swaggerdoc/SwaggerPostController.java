package ru.skillbox.zerone.backend.controller.swaggerdoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.skillbox.zerone.backend.model.dto.request.PostRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.PostDTO;

@Tag(name = "Контроллер для работы с постами")
public interface SwaggerPostController {
  @Operation(summary = "Создание поста на стене пользователя")
  @ApiResponse(responseCode = "200", description = "Пост успешно создан")
  @ApiResponse(responseCode = "400", description = "Некорректные данные для создания поста")
  CommonResponseDTO<PostDTO> getUserWall(
      @PathVariable int id,
      @RequestParam(name = "publish_date", defaultValue = "0") long publishDate,
      @RequestBody @Valid PostRequestDTO postRequestDTO
  );

  @Operation(summary = "Получение списка постов на стене пользователя")
  @ApiResponse(responseCode = "200", description = "Список постов успешно получен")
  CommonListResponseDTO<PostDTO> getUserWall(
      @PathVariable int id,
      @RequestParam(name = "offset", defaultValue = "0") int offset,
      @RequestParam(name = "itemPerPage", defaultValue = "10") int itemPerPage
  );

  @Operation(summary = "Получение списка новостей")
  @ApiResponse(responseCode = "200", description = "Список новостей успешно получен")
  CommonListResponseDTO<PostDTO> getFeeds(
      @RequestParam(name = "offset", defaultValue = "0") int offset,
      @RequestParam(name = "itemPerPage", defaultValue = "20") int itemPerPage
  );

  @Operation(summary = "Получение списка постов")
  @ApiResponse(responseCode = "200", description = "Список постов успешно получен")
  CommonListResponseDTO<PostDTO> getPosts(
      @RequestParam(name = "text", required = false) String text,
      @RequestParam(name = "author", required = false) String author,
      @RequestParam(name = "tag", required = false) String tag,
      @RequestParam(name = "date_from", defaultValue = "0") Long dateFrom,
      @RequestParam(name = "offset", defaultValue = "0") int offset,
      @RequestParam(name = "itemPerPage", defaultValue = "10") int itemPerPage
  );

  @Operation(summary = "Получение поста по id")
  @ApiResponse(responseCode = "200", description = "Пост успешно найден")
  @ApiResponse(responseCode = "400", description = "Некорректный id поста")
  CommonResponseDTO<PostDTO> getPostById(@PathVariable int id);
  @Operation(summary = "Изменение поста")
  @ApiResponse(responseCode = "200", description = "Пост успешно изменен")
  @ApiResponse(responseCode = "400", description = "Некорректные данные для изменения поста")
  CommonResponseDTO<PostDTO> putPostById(
      @PathVariable int id,
      @RequestParam(name = "publish_date", required = false, defaultValue = "0") Long publishDate,
      @RequestBody @Valid PostRequestDTO postRequestDTO
  );

  @Operation(summary = "Удаление поста по id")
  @ApiResponse(responseCode = "200", description = "Пост успешно удален")
  @ApiResponse(responseCode = "400", description = "Некорректный id поста")
  CommonResponseDTO<PostDTO> deletePostById(@PathVariable int id);

  @Operation(summary = "Восстановление удаленного поста по id")
  @ApiResponse(responseCode = "200", description = "Пост успешно восстановлен")
  @ApiResponse(responseCode = "400", description = "Некорректный id поста")
  CommonResponseDTO<PostDTO> putPostRecover(@PathVariable Long id);
}