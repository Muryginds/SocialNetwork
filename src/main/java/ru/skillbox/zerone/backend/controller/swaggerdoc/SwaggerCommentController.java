package ru.skillbox.zerone.backend.controller.swaggerdoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.skillbox.zerone.backend.model.dto.request.CommentRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommentDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;

public interface SwaggerCommentController{

  @Operation(summary = "Получение списка комментариев к посту")
  @ApiResponse(responseCode = "200", description = "Список комментариев успешно получен")
  CommonListResponseDTO<CommentDTO> getFeeds(
      @PathVariable int id,
      @RequestParam(name = "offset", defaultValue = "0") int offset,
      @RequestParam(name = "itemPerPage", defaultValue = "5") int itemPerPage
  );

  @Operation(summary = "Добавление комментария к посту")
  @ApiResponse(responseCode = "200", description = "Комментарий успешно добавлен")
  @ApiResponse(responseCode = "400", description = "Некорректные данные для добавления комментария")
  CommonResponseDTO<CommentDTO> addComment(
      @PathVariable int id,
      @RequestBody @Valid CommentRequestDTO commentRequest
  );

  @Operation(summary = "Восстановление удаленного комментария к посту")
  @ApiResponse(responseCode = "200", description = "Комментарий успешно восстановлен")
  @ApiResponse(responseCode = "400", description = "Некорректные данные для восстановления комментария")
  CommonResponseDTO<CommentDTO> recoveryComment(
      @PathVariable int id,
      @PathVariable(name = "comment_id") int commentId
  );

  @Operation(summary = "Изменение комментария к посту")
  @ApiResponse(responseCode = "200", description = "Комментарий успешно изменен")
  @ApiResponse(responseCode = "400", description = "Некорректные данные для изменения комментария")
  CommonResponseDTO<CommentDTO> putComment(
      @PathVariable int id,
      @PathVariable(name = "comment_id") int commentId,
      @RequestBody @Valid CommentRequestDTO commentRequest
  );

  @Operation(summary = "Удаление комментария к посту")
  @ApiResponse(responseCode = "200", description = "Комментарий успешно удален")
  @ApiResponse(responseCode = "400", description = "Некорректный id поста или комментария")
  CommonResponseDTO<CommentDTO> deleteComment(
      @PathVariable(name = "id") long postId,
      @PathVariable(name = "comment_id") int commentId
  );
}