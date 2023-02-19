package ru.skillbox.zerone.backend.controller;

import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.exception.PostCreationExecption;
import ru.skillbox.zerone.backend.model.dto.request.PostRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.PostsDTO;
import ru.skillbox.zerone.backend.service.PostService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1")
public class PostController {
  private PostService postService;
  public PostController(PostService postService) {
    this.postService = postService;
  }
  @PostMapping("/users/{id}/wall")
  public CommonResponseDTO<PostsDTO> getUserWall (@PathVariable int id,
                                                  @RequestParam(name = "publish_date", defaultValue = "0") long publishDate,
                                                  @RequestBody PostRequestDTO postRequestDTO, Principal principal)  throws PostCreationExecption {
    return postService.createPost(id, publishDate, postRequestDTO, principal);
  }
  @GetMapping("/feeds")
  public CommonListDTO<PostsDTO> getFeeds (@RequestParam(name = "text", defaultValue = "") String text,
                                           @RequestParam(name = "offset", defaultValue = "0") int offset,
                                           @RequestParam(name = "itemPerPage", defaultValue = "20") int itemPerPage,
                                           Principal principal){
    return postService.getFeeds (text,offset,itemPerPage);
  }
}
