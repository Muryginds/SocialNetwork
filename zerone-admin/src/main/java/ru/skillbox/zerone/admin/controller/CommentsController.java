package ru.skillbox.zerone.admin.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.skillbox.zerone.admin.model.dto.CommentDescriptionDto;
import ru.skillbox.zerone.admin.model.dto.CommentModerationDto;
import ru.skillbox.zerone.admin.model.dto.ErrorDto;
import ru.skillbox.zerone.admin.model.dto.TotalCommentDto;
import ru.skillbox.zerone.admin.service.CommentsService;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/admin/comments")
public class CommentsController {
  private static final String COMMENT_DESCRIPTION = "comment-description";
  private static final String COMMENTS_REDIRECT = "redirect:{}/api/v1/admin/comments";
  private static final String COMMENT_CHOSEN = "redirect:{}/api/v1/admin/comments/chosen";
  private static final String DESCRIPTION_DTO = "descriptionDto";
  private static final String TOTAL_COMMENT_DTO = "totalCommentDto";
  private final CommentsService commentsService;
  private final String apiGatewayHost;

  @GetMapping
  public String getCommentDescription(Model model, HttpServletRequest req) {
    CommentDescriptionDto descriptionDto = (CommentDescriptionDto) req.getSession()
        .getAttribute(DESCRIPTION_DTO);
    if (descriptionDto == null) {
      descriptionDto = new CommentDescriptionDto();
    }
    model.addAttribute("description", descriptionDto);
    model.addAttribute("paramError", descriptionDto.getParamError());

    req.getSession().removeAttribute(DESCRIPTION_DTO);

    return COMMENT_DESCRIPTION;
  }

  @PostMapping
  public String postCommentDescription(HttpServletRequest req, CommentDescriptionDto descriptionDto) {
    req.getSession().removeAttribute(DESCRIPTION_DTO);
    ErrorDto errorDto = commentsService.checkParams(descriptionDto);
    if (errorDto != null) {
      descriptionDto.setParamError(errorDto.getValue());
      req.getSession().setAttribute(DESCRIPTION_DTO, descriptionDto);
      return MessageFormatter.format(COMMENTS_REDIRECT, apiGatewayHost).getMessage();
    }
    TotalCommentDto totalCommentDto = commentsService.postCommentDescription(descriptionDto);
    if (totalCommentDto == null) {
      descriptionDto.setParamError("Такой фрагмент не найден");
      req.getSession().setAttribute(DESCRIPTION_DTO, descriptionDto);
      return MessageFormatter.format(COMMENTS_REDIRECT, apiGatewayHost).getMessage();
    }
    req.getSession().setAttribute(TOTAL_COMMENT_DTO, totalCommentDto);
    return MessageFormatter.format(COMMENT_CHOSEN, apiGatewayHost).getMessage();
  }

  @GetMapping(value = "/chosen")
  public String commentChosen(Model model, HttpServletRequest req) {
    TotalCommentDto totalCommentDto = (TotalCommentDto) req.getSession()
        .getAttribute(TOTAL_COMMENT_DTO);
    model.addAttribute("totalDto", totalCommentDto);
    return "comment-list";
  }

  @GetMapping("/{id}")
  public String getCommentEdit(@PathVariable Long id, Model model) {
    CommentModerationDto commentDto = commentsService.getCommentEdit(id);
    model.addAttribute("commentDto", commentDto);
    return "comment-edit";
  }

  @PostMapping("/comment")
  public String postCommentEdit(CommentModerationDto commentDto) {
    commentsService.postCommentEdit(commentDto);
    return MessageFormatter.format(COMMENTS_REDIRECT, apiGatewayHost).getMessage();
  }
}
