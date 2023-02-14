package ru.skillbox.zerone.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.zerone.backend.model.dto.request.PostRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.ListPostsDTO;
import ru.skillbox.zerone.backend.model.dto.response.PostsDTO;
import ru.skillbox.zerone.backend.service.PostService;

@RestController
@RequestMapping("/api/v1")
public class PostController {
  private PostService postService;
  public PostController(PostService postService) {
    this.postService = postService;
  }
  @PostMapping("/users/{id}/wall")
  public ListPostsDTO <PostsDTO> createPost(int id, long publishDate, PostRequestDTO postRequestDTO) {
    return postService.createPost(id, publishDate, postRequestDTO);
  }
  @GetMapping("/feeds")
  public ListPostsDTO <PostsDTO> getFeeds (String text, int offset, int itemPerPege){
    return postService.getFeeds (text,offset,itemPerPege);
  }
}
