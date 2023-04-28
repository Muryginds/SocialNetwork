package ru.skillbox.zerone.backend.controller.swaggerdoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.model.dto.request.PostRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.PostDTO;

@RestController
@Tag(name = "Контроллер для работы с постами")
@RequestMapping("/api/v1")
public interface SwaggerPostController {
  @Operation(summary = "Создать пост на стене")
  @PostMapping("/users/{id}/wall")
  CommonResponseDTO<PostDTO> getUserWall(@PathVariable long id,
                                         @RequestParam(name = "publish_date", defaultValue = "0") long publishDate,
                                         @RequestBody PostRequestDTO postRequestDTO);

  @Operation(summary = "Получить посты на стене")
  @GetMapping("/users/{id}/wall")
  CommonListResponseDTO<PostDTO> getUserWall(@PathVariable long id,
                                             @RequestParam(name = "offset", defaultValue = "0") int offset,
                                             @RequestParam(name = "itemPerPage", defaultValue = "10") int itemPerPage);

  @Operation(summary = "Получить посты новостях")
  @GetMapping("/feeds")
  CommonListResponseDTO<PostDTO> getFeeds(
      @RequestParam(name = "offset", defaultValue = "0") int offset,
      @RequestParam(name = "itemPerPage", defaultValue = "20") int itemPerPage);

  @Operation(summary = "Получить посты в поиске")
  @GetMapping("/post")
  CommonListResponseDTO<PostDTO> getPosts(@RequestParam(name = "text", required = false) String text,
                                          @RequestParam(name = "author", required = false) String author,
                                          @RequestParam(name = "tag", required = false) String tag,
                                          @RequestParam(name = "date_from", defaultValue = "0") Long dateFrom,
                                          @RequestParam(name = "offset", defaultValue = "0") int offset,
                                          @RequestParam(name = "itemPerPage", defaultValue = "10") int itemPerPage);

  @Operation(summary = "Получить пост")
  @GetMapping("/post/{id}")
  CommonResponseDTO<PostDTO> getPostById(@PathVariable long id);

  @Operation(summary = "Изменить пост")
  @PutMapping("/post/{id}")
  CommonResponseDTO<PostDTO> putPostById(@PathVariable long id,
                                         @RequestParam(name = "publish_date", required = false, defaultValue = "0") Long publishDate,
                                         @RequestBody PostRequestDTO requestBody);

  @Operation(summary = "Удалить пост")
  @DeleteMapping("/post/{id}")
  CommonResponseDTO<PostDTO> deletePostById(@PathVariable long id);

  @Operation(summary = "Восстановить пост")
  @PutMapping("/post/{id}/recover")
  CommonResponseDTO<PostDTO> putPostRecover(@PathVariable Long id);
}
