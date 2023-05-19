package ru.skillbox.zerone.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.zerone.backend.controller.swaggerdoc.SwaggerCommentController;
import ru.skillbox.zerone.backend.model.dto.request.CommentRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommentDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.service.CommentService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CommentController implements SwaggerCommentController {
  private final CommentService commentService;

  @GetMapping("/post/{id}/comments")
  public CommonListResponseDTO<CommentDTO> getFeeds(@RequestParam(name = "offset", defaultValue = "0") int offset,
                                                    @RequestParam(name = "itemPerPage", defaultValue = "5") int itemPerPage,
                                                    @PathVariable long id) {
    return commentService.getComments(offset, itemPerPage, id);
  }

  @PostMapping("/post/{id}/comments")
  public CommonResponseDTO<CommentDTO> addComment(@PathVariable long id,
                                                  @RequestBody CommentRequestDTO commentRequest) {
    return commentService.addComment(id, commentRequest);
  }

  @DeleteMapping("/post/{id}/comments/{comment_id}")
  public CommonResponseDTO<CommentDTO> deleteComment(@PathVariable long id,
                                                     @PathVariable(name = "comment_id") long commentId) {
    return commentService.deleteComment(commentId);
  }

  @PutMapping("/post/{id}/comments/{comment_id}/recover")
  public CommonResponseDTO<CommentDTO> recoveryComment(@PathVariable long id,
                                                       @PathVariable(name = "comment_id") long commentId) {
    return commentService.recoveryComment(commentId);
  }

  @PutMapping("/post/{id}/comments/{comment_id}")
  public CommonResponseDTO<CommentDTO> putComment(@PathVariable long id,
                                                  @PathVariable(name = "comment_id") long commentId,
                                                  @RequestBody CommentRequestDTO commentRequest) {
    return commentService.updateComment(id, commentId, commentRequest);
  }
}

