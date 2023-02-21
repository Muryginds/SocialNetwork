package ru.skillbox.zerone.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.LanguageDTO;
import ru.skillbox.zerone.backend.service.PlatformService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/platform")
@RequiredArgsConstructor
public class PlatformController {
  private final PlatformService platformService;
  @GetMapping("/languages")
  public CommonListResponseDTO<LanguageDTO> getLanguages() {
    return platformService.getLanguages();
  }

}
