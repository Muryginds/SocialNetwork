package ru.skillbox.zerone.backend.controller.swaggerdoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.model.dto.request.CommentRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommentDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;

@RestController
@Tag(name = "Контроллер для работы с коментариями")
@RequestMapping("/api/v1")
public interface SwaggerCommentController {
  @Operation(summary = "Получить коментарии")
  @GetMapping("/post/{id}/comments")
  CommonListResponseDTO<CommentDTO> getFeeds(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                             @RequestParam(name = "itemPerPage", defaultValue = "5") int itemPerPage,
                                             @PathVariable int id);

  @Operation(summary = "Написать коментарий")
  @PostMapping("/post/{id}/comments")
  CommonResponseDTO<CommentDTO> addComment(@PathVariable long id,
                                           @RequestBody CommentRequestDTO commentRequest);

  @Operation(summary = "Удалить коментарий")
  @DeleteMapping("/post/{id}/comments/{comment_id}")
  CommonResponseDTO<CommentDTO> deleteComment(@PathVariable long id,
                                              @PathVariable(name = "comment_id") long commentId);

  @Operation(summary = "Восстановить коментарий")
  @PutMapping("/post/{id}/comments/{comment_id}/recover")
  CommonResponseDTO<CommentDTO> recoveryComment(@PathVariable long id,
                                                @PathVariable(name = "comment_id") long commentId);

  @Operation(summary = "Редактировать коментарий")
  @PutMapping("/post/{id}/comments/{comment_id}")
  CommonResponseDTO<CommentDTO> putComment(@PathVariable long id,
                                           @PathVariable(name = "comment_id") long commentId,
                                           @RequestBody CommentRequestDTO commentRequest);

}
