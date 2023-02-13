package ru.skillbox.zerone.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.zerone.backend.model.dto.request.SupportRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.service.SupportRequestService;

@RestController
@RequestMapping(path = "api/v1/support")
@RequiredArgsConstructor
public class SupportRequestController {
  private final SupportRequestService supportRequestService;

  @PostMapping
  public CommonResponseDTO<MessageResponseDTO> registerSupportRequest(
      @Valid @RequestBody SupportRequestDTO supportRequestDTO) {
    return supportRequestService.registerSupportRequest(supportRequestDTO);
  }
}
