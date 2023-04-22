package ru.skillbox.zerone.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/api/v1/admin/all-services")
public class AllServicesController {

  @GetMapping
  public String getAllServices() {
    return "all-services";
  }
}
