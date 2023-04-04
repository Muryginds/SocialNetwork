package ru.skillbox.zerone.backend.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.zerone.backend.controller.swaggerdoc.SwaggerStorageController;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.StorageDTO;
import ru.skillbox.zerone.backend.service.StorageService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class StorageController implements SwaggerStorageController {
  private final StorageService storageService;

  @PostMapping("/storage")
  public CommonResponseDTO<StorageDTO> postImage(@NotNull MultipartFile file) {
    return storageService.uploadImage(file);
  }
  @DeleteMapping("/storage/{public_id}")
  public CommonResponseDTO<String> deleteImage(@PathVariable("public_id") String publicId) {
    return storageService.deleteImage(publicId);
  }
}

