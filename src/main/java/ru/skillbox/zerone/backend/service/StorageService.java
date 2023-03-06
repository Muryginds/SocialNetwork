package ru.skillbox.zerone.backend.service;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.zerone.backend.model.entity.File;
import ru.skillbox.zerone.backend.repository.FileRepository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;


@RequiredArgsConstructor
@Service
public class StorageService {
  private final Cloudinary cloudinary;
  private final FileRepository fileRepository;
  private final static Map options = Map.of();

  public String uploadImage(MultipartFile file) {
    try {
      var uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
      // Получаем информацию о загруженном файле
      String publicId = (String) uploadResult.get("public_id");
      String secureUrl = (String) uploadResult.get("secure_url");
      String format = (String) uploadResult.get("format");
      // Создаем объект файла и сохраняем его в БД
      File fileUp = new File();
      fileUp.setPublicId(publicId)
      .setUrl(secureUrl)
      .setFormat(format);
      fileRepository.save(fileUp);
      return secureUrl;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
