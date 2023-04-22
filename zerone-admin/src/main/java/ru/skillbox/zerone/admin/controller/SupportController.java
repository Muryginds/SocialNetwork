package ru.skillbox.zerone.admin.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.skillbox.zerone.admin.model.dto.ChoiceDto;
import ru.skillbox.zerone.admin.model.dto.ChoiceListDto;
import ru.skillbox.zerone.admin.service.SupportService;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/admin/support")
public class SupportController {
  private final SupportService supportService;
  private final String apiGatewayHost;

  @GetMapping(value = "/choice")
  public String doChoice(Model model) {
    ChoiceListDto requests = supportService.getChoiceListDto();
    model.addAttribute("requests", requests);
    return "support-choice";
  }

  @GetMapping("/answer/{id}")
  public String doAnswer(@PathVariable Long id, Model model) {
    ChoiceDto dto = supportService.getRequestById(id);
    model.addAttribute("request", dto);
    supportService.sendEmail(dto);
    return "support-answer";
  }

  @PostMapping("/answer/{id}")
  public String answer(@PathVariable Long id, ChoiceDto choiceDto) {
    choiceDto.setId(id);
    supportService.processAnswer(choiceDto);
    return MessageFormatter.format("redirect:{}/api/v1/admin/support/choice", apiGatewayHost).getMessage();
  }
}
