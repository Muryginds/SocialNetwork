package ru.skillbox.zerone.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/admin")
public class LoginController {

  @GetMapping("/login")
  public String getLogin() {
    return "login";
  }

  @GetMapping("/logout")
  public String doLogout() {
    return "login";
  }
}
