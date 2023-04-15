package ru.skillbox.zerone.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.model.dto.request.PostRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.PostDTO;
import ru.skillbox.zerone.backend.service.PostService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PostController {
  private final PostService postService;

  @PostMapping("/users/{id}/wall")
  public CommonResponseDTO<PostDTO> getUserWall(@PathVariable long id,
                                                @RequestParam(name = "publish_date", defaultValue = "0") long publishDate,
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
  public CommonResponseDTO<PostDTO> getPostById(@PathVariable int id) {
    return postService.getPostById(id);
  }

  @PutMapping("/post/{id}")
  public CommonResponseDTO<PostDTO> putPostById(@PathVariable int id,
                                                @RequestParam(name = "publish_date", required = false, defaultValue = "0") Long publishDate,
                                                @RequestBody PostRequestDTO requestBody) {
    return postService.putPostById(id, publishDate, requestBody);
  }

  @DeleteMapping("/post/{id}")
  public CommonResponseDTO<PostDTO> deletePostById(@PathVariable int id) {
    return postService.deletePostById(id);
  }

  @PutMapping("/post/{id}/recover")
  public CommonResponseDTO<PostDTO> putPostRecover(@PathVariable Long id) {
    return postService.putPostIdRecover(id);
  }
}
