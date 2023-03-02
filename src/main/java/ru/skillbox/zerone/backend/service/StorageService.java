package ru.skillbox.zerone.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.StorageDTO;

import java.io.IOException;
import java.util.Map;


@RequiredArgsConstructor
@Service
@Slf4j
public class StorageService {
  private final Cloudinary cloudinary;

  // Загрузка изображения
  public String uploadImage(MultipartFile file) throws IOException {
    Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
    return (String) uploadResult.get("secure_url");
  }
  public CommonResponseDTO<StorageDTO> uploadFileUrl(MultipartFile file) throws IOException {
    StorageDTO storageDTO = new StorageDTO();
    storageDTO.setUrl(uploadImage(file));
    return CommonResponseDTO.<StorageDTO>builder().data(storageDTO).build();
  }
}