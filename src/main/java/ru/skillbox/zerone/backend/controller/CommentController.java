package ru.skillbox.zerone.backend.controller;

import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.exception.CommentNotFoundException;
import ru.skillbox.zerone.backend.exception.PostNotFoundException;
import ru.skillbox.zerone.backend.model.dto.request.CommentRequest;
import ru.skillbox.zerone.backend.model.dto.response.CommentDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.service.CommentService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1")
public class CommentController {
  private final CommentService commentService;

  public CommentController(CommentService commentService) {
    this.commentService = commentService;
  }

  @GetMapping("/post/{id}/comments")

  public CommonListDTO<CommentDTO> getFeeds(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                            @RequestParam(name = "itemPerPage", defaultValue = "5") int itemPerPage,
                                            @PathVariable int id,
                                            Principal principal) throws PostNotFoundException {
    return commentService.getComments(offset, itemPerPage, id, principal);
  }

  @PostMapping("/post/{id}/comments")
  public CommonResponseDTO<CommentDTO> comment(@PathVariable int id,
                                               @RequestBody CommentRequest commentRequest,
                                               Principal principal)  {
    return commentService.comment(id, commentRequest, principal);
  }

  @DeleteMapping("/post/{id}/comments/{comment_id}")
  public CommonResponseDTO<CommentDTO> deleteComment(@PathVariable int id,
                                                     @PathVariable(name = "comment_id") int commentId,
                                                     Principal principal) throws CommentNotFoundException {
    return commentService.deleteComment(commentId, principal);
  }
  @PutMapping("/post/{id}/comments/{comment_id}/recover")
  public CommonResponseDTO<CommentDTO> recoveryComment(@PathVariable int id,
                                                       @PathVariable(name = "comment_id") int commentId,
                                                       Principal principal) throws CommentNotFoundException {
    return commentService.recoveryComment(commentId, principal);
  }
  @PutMapping("/post/{id}/comments/{comment_id}")
  public CommonResponseDTO<CommentDTO> putComment(@PathVariable int id,
                                                  @PathVariable(name = "comment_id") int commentId,
                                                  @RequestBody CommentRequest commentRequest,
                                                  Principal principal) throws PostNotFoundException, CommentNotFoundException {
    return commentService.putComment(id, commentId, commentRequest, principal);
  }

}

