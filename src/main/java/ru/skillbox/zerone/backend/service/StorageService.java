package ru.skillbox.zerone.backend.service;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.StorageDTO;
import ru.skillbox.zerone.backend.model.entity.File;
import ru.skillbox.zerone.backend.repository.FileRepository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class StorageService {
  private static final Map<Object, Object> options = Map.of();
  private final Cloudinary cloudinary;
  private final FileRepository fileRepository;

  public CommonResponseDTO<StorageDTO> uploadImage(MultipartFile file) {
    try {
      var uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
      // Получаем информацию о загруженном файле
      String publicId = (String) uploadResult.get("public_id");
      String secureUrl = (String) uploadResult.get("secure_url");
      String format = (String) uploadResult.get("format");
      // Создаем объект файла и сохраняем его в БД
      File fileUp = new File()
          .setPublicId(publicId)
          .setUrl(secureUrl)
          .setFormat(format);
      fileRepository.save(fileUp);
      StorageDTO storageDTO = new StorageDTO();
      storageDTO.setUrl(secureUrl);
      return CommonResponseDTO.<StorageDTO>builder().data(storageDTO).build();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
  @Transactional
  public CommonResponseDTO<String> deleteImage(String publicId) {
    try {
      // Удаляем изображение из Cloudinary
      cloudinary.uploader().destroy(publicId, options);
      // Удаляем изображение из БД
      fileRepository.deleteByPublicId(publicId);
      return CommonResponseDTO.<String>builder().data("Image deleted successfully").build();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
