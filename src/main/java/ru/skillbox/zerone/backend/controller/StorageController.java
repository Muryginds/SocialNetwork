package ru.skillbox.zerone.backend.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.StorageDTO;
import ru.skillbox.zerone.backend.service.StorageService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class StorageController {
  private final StorageService storageService;

  @PostMapping("/storage")
  public CommonResponseDTO<StorageDTO> postImage(@NotNull MultipartFile file) {
    return storageService.uploadImage(file);
  }
}

