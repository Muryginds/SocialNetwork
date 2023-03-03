package ru.skillbox.zerone.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.StorageDTO;
import ru.skillbox.zerone.backend.service.StorageService;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/v1")
public class StorageController {
  private final StorageService cloudinaryService;
  @PostMapping("/storage")
  public ResponseEntity<CommonResponseDTO<StorageDTO>> postImage(@NotNull MultipartFile file) throws IOException {
    return new ResponseEntity<>(cloudinaryService.uploadFileUrl(file), HttpStatus.OK);
  }
}
