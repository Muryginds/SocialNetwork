package ru.skillbox.zerone.backend.controller;

import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.exception.PostCreationExecption;
import ru.skillbox.zerone.backend.exception.PostNotFoundException;
import ru.skillbox.zerone.backend.exception.UserAndAuthorEqualsException;
import ru.skillbox.zerone.backend.model.dto.request.PostRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.PostsDTO;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.service.PostService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1")
public class PostController {
  private final PostService postService;
  public PostController(PostService postService) {
    this.postService = postService;
  }
  @PostMapping("/users/wall")
  public CommonResponseDTO<PostsDTO> getUserWall (@PathVariable int id,
                                                  @RequestParam(name = "publish_date", defaultValue = "0") long publishDate,
                                                  @RequestBody PostRequestDTO postRequestDTO, Principal principal)  throws PostCreationExecption {
    return postService.createPost(id, publishDate, postRequestDTO, principal);
  }
  @GetMapping("/users/{id}/wall")
  public CommonListDTO<PostsDTO> getUserWall (@PathVariable int id,
                                                  @RequestParam (name = "offset", defaultValue = "0") int offset,
                                              @RequestParam(name = "itemPerPage", defaultValue = "10") int itemPerPage, Principal principal)  {
    return postService.getAuthorWall(id, offset, itemPerPage, principal);
  }
  @GetMapping("/feeds")
  public CommonListDTO<PostsDTO> getFeeds (@RequestParam(name = "text", defaultValue = "") String text,
                                           @RequestParam(name = "offset", defaultValue = "0") int offset,
                                           @RequestParam(name = "itemPerPage", defaultValue = "20") int itemPerPage, Principal principal){
    return postService.getFeeds (text,offset,itemPerPage);
  }
  @GetMapping("/post")
  public CommonListDTO<PostsDTO> getPosts (@RequestParam(name = "text", defaultValue = "") String text,
                                           @RequestParam(name = "date_from", defaultValue = "-1") long dateFrom,
                                           @RequestParam(name = "date_to", defaultValue = "-1") long dateTo,
                                           @RequestParam(name = "offset", defaultValue = "0") int offset,
                                           @RequestParam(name = "itemPerPage", defaultValue = "20") int itemPerPage,
                                           @RequestParam(name = "author", defaultValue = "") String author,
                                           @RequestParam(name = "tag", defaultValue = "") String tag,
                                           Principal principal) {
    return postService.getPosts (text, dateFrom, dateTo, offset, itemPerPage, author, tag, principal);
  }
  @GetMapping("/post/{id}")
  public CommonResponseDTO<PostsDTO> getPostById(@PathVariable int id, Principal principal) throws PostNotFoundException {
    return postService.getPostById(id, principal);
  }
  @PutMapping("/post/{id}")
//  public CommonResponseDTO<PostsDTO> putPostById(@PathVariable int id,
//                                                 @RequestParam(name = "publish_date", required = false, defaultValue = "0") long publishDate,
//                                                 @RequestBody PostRequestDTO requestBody,
//                                                 Principal principal) throws PostNotFoundException, UserAndAuthorEqualsException {
//    return postService.putPostById(id, publishDate, requestBody, principal);
//  }
  @DeleteMapping("/post/{id}")
  public CommonResponseDTO<PostsDTO> deletePostById(@PathVariable int id,
                                                       Principal principal) throws PostNotFoundException, UserAndAuthorEqualsException {
    return postService.deletePostById(id, principal);
  }
//  @PutMapping("/post/{id}")
//  public CommonResponseDTO<PostsDTO> putPostRecover(@PathVariable int id,
//                                                    Principal principal) throws PostNotFoundException, UserAndAuthorEqualsException {
//    return postService.putPostIdRecover(id, principal);
//  }
}
