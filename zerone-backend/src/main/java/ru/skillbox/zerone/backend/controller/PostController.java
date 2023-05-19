package ru.skillbox.zerone.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.controller.swaggerdoc.SwaggerPostController;
import ru.skillbox.zerone.backend.model.dto.request.PostRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.PostDTO;
import ru.skillbox.zerone.backend.service.PostService;

import java.beans.PropertyEditorSupport;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;


@Tag(name = "Контроллер для работы с постами")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
class PostController implements SwaggerPostController {
  private final PostService postService;

  @InitBinder
  public void initBinder(final WebDataBinder webDataBinder) {
    webDataBinder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String time) throws IllegalArgumentException {
        setValue(LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(time)), ZoneId.systemDefault()));
      }
    });
  }

  @PostMapping("/users/{id}/wall")
  public CommonResponseDTO<PostDTO> createPostOnWall(@PathVariable long id,
                                                     @RequestParam(name = "publish_date", defaultValue = "0") LocalDateTime publishDate,
                                                     @RequestBody PostRequestDTO postRequestDTO) {
    return postService.createPost(id, publishDate, postRequestDTO);
  }


  @GetMapping("/users/{id}/wall")
  public CommonListResponseDTO<PostDTO> getUserWall(@PathVariable int id,
                                                    @RequestParam(name = "offset", defaultValue = "0") int offset,
                                                    @RequestParam(name = "itemPerPage", defaultValue = "10") int itemPerPage) {
    return postService.getAuthorWall(id, offset, itemPerPage);

  }


  @GetMapping("/feeds")
  public CommonListResponseDTO<PostDTO> getFeeds(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                                 @RequestParam(name = "itemPerPage", defaultValue = "20") int itemPerPage) {
    return postService.getFeeds(offset, itemPerPage);
  }


  @GetMapping("/post")
  public CommonListResponseDTO<PostDTO> getPosts(@RequestParam(name = "text", required = false) String text,
                                                 @RequestParam(name = "author", required = false) String author,
                                                 @RequestParam(name = "tag", required = false) String tag,
                                                 @RequestParam(name = "date_from", defaultValue = "0") Long dateFrom,
                                                 @RequestParam(name = "offset", defaultValue = "0") int offset,
                                                 @RequestParam(name = "itemPerPage", defaultValue = "10") int itemPerPage) {
    return postService.getPosts(text, author, tag, dateFrom, offset, itemPerPage);
  }


  @GetMapping("/post/{id}")
  public CommonResponseDTO<PostDTO> getPostById(@PathVariable long id) {
    return postService.getPostById(id);
  }


  @PutMapping("/post/{id}")
  public CommonResponseDTO<PostDTO> putPostById(@PathVariable int id,
                                                @RequestParam(name = "publish_date", required = false, defaultValue = "0") LocalDateTime publishDate,
                                                @RequestBody PostRequestDTO requestBody) {
    return postService.putPostById(id, publishDate, requestBody);
  }


  @DeleteMapping("/post/{id}")
  public CommonResponseDTO<PostDTO> deletePostById(@PathVariable int id) {
    return postService.deletePostById(id);
  }


  @Operation(summary = "Восстановить пост")
  @PutMapping("/post/{id}/recover")
  public CommonResponseDTO<PostDTO> putPostRecover(@PathVariable Long id) {
    return postService.putPostIdRecover(id);
  }
}
