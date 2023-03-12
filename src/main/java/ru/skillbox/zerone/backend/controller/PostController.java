package ru.skillbox.zerone.backend.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.model.dto.request.PostRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.PostsDTO;
import ru.skillbox.zerone.backend.service.PostService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PostController {
  private final PostService postService;

  @PostMapping("/users/{id}/wall")
  public CommonResponseDTO<PostsDTO> getUserWall (@PathVariable int id,
                                                  @RequestParam(name = "publish_date", defaultValue = "0") long publishDate,
                                                  @RequestBody PostRequestDTO postRequestDTO) {
    return postService.createPost(id, publishDate, postRequestDTO);
  }
  @GetMapping("/users/{id}/wall")
  public CommonListResponseDTO<PostsDTO> getUserWall (@PathVariable int id,
                                              @RequestParam (name = "offset", defaultValue = "0") int offset,
                                              @RequestParam(name = "itemPerPage", defaultValue = "10") int itemPerPage)  {
    return postService.getAuthorWall(id, offset, itemPerPage);

  }
  @GetMapping("/feeds")
  public CommonListResponseDTO<PostsDTO> getFeeds (@RequestParam(name = "text", defaultValue = "") String text,
                                           @RequestParam(name = "offset", defaultValue = "0") int offset,
                                           @RequestParam(name = "itemPerPage", defaultValue = "20") int itemPerPage){
    return postService.getFeeds (text,offset,itemPerPage);
  }
  @GetMapping("/post")
  public CommonListResponseDTO<PostsDTO> getPosts (@RequestParam(name = "text", defaultValue = "") String text,
                                           @RequestParam(name = "date_from", defaultValue = "-1") long dateFrom,
                                           @RequestParam(name = "date_to", defaultValue = "-1") long dateTo,
                                           @RequestParam(name = "offset", defaultValue = "0") int offset,
                                           @RequestParam(name = "itemPerPage", defaultValue = "20") int itemPerPage,
                                           @RequestParam(name = "author", defaultValue = "") String author,
                                           @RequestParam(name = "tag", defaultValue = "") String tag) {
    return postService.getPosts (text, dateFrom, dateTo, offset, itemPerPage, author, tag);
  }
  @GetMapping("/post/{id}")
  public CommonResponseDTO<PostsDTO> getPostById(@PathVariable int id){
    return postService.getPostById(id);
  }
  @PutMapping("/post/{id}")
  public CommonResponseDTO<PostsDTO> putPostById(@PathVariable int id,
                                                 @RequestParam(name = "publish_date", required = false, defaultValue = "0") Long publishDate,
                                                 @RequestBody PostRequestDTO requestBody) {
    return postService.putPostById(id, publishDate, requestBody);
  }
  @DeleteMapping("/post/{id}")
  public CommonResponseDTO<PostsDTO> deletePostById(@PathVariable int id) {
    return postService.deletePostById(id);
  }
  @PutMapping("/post/{id}/recover")
  public CommonResponseDTO<PostsDTO> putPostRecover(@PathVariable Long id) {
    return postService.putPostIdRecover(id);
  }
}
