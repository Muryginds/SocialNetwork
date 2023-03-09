package ru.skillbox.zerone.backend.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.exception.CommentNotFoundException;
import ru.skillbox.zerone.backend.exception.PostNotFoundException;
import ru.skillbox.zerone.backend.model.dto.request.CommentRequest;
import ru.skillbox.zerone.backend.model.dto.response.CommentDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.service.CommentService;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class CommentController {
  private final CommentService commentService;

  @GetMapping("/post/{id}/comments")
  public CommonListDTO<CommentDTO> getFeeds(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                            @RequestParam(name = "itemPerPage", defaultValue = "5") int itemPerPage,
                                            @PathVariable int id) throws PostNotFoundException {
    return commentService.getComments(offset, itemPerPage, id);
  }

  @PostMapping("/post/{id}/comments")
  public CommonResponseDTO<CommentDTO> comment(@PathVariable int id,
                                               @RequestBody CommentRequest commentRequest)  {
    return commentService.comment(id, commentRequest);
  }

  @DeleteMapping("/post/{id}/comments/{comment_id}")
  public CommonResponseDTO<CommentDTO> deleteComment(@PathVariable long id,
                                                     @PathVariable(name = "comment_id") int commentId) throws CommentNotFoundException {
    return commentService.deleteComment(commentId);
  }
  @PutMapping("/post/{id}/comments/{comment_id}/recover")
  public CommonResponseDTO<CommentDTO> recoveryComment(@PathVariable int id,
                                                       @PathVariable(name = "comment_id") int commentId) throws CommentNotFoundException {
    return commentService.recoveryComment(commentId);
  }
  @PutMapping("/post/{id}/comments/{comment_id}")
  public CommonResponseDTO<CommentDTO> putComment(@PathVariable int id,
                                                  @PathVariable(name = "comment_id") int commentId,
                                                  @RequestBody CommentRequest commentRequest) throws PostNotFoundException, CommentNotFoundException {
    return commentService.putComment(id, commentId, commentRequest);
  }

}

