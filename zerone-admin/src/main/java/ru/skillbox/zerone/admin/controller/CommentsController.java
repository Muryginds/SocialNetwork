package ru.skillbox.zerone.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/admin/comments")
public class CommentsController {

  @GetMapping
  public String getCommentParams(Model model) {
    return "comments";
  }
}
