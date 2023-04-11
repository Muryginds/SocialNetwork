package ru.skillbox.zerone.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.StorageDTO;
import ru.skillbox.zerone.backend.model.entity.File;
import ru.skillbox.zerone.backend.repository.FileRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Service
public class StorageService {
  private final Cloudinary cloudinary;
  private final FileRepository fileRepository;

  public CommonResponseDTO<StorageDTO> uploadImage(MultipartFile file) {
    File storage;
    try {
      BufferedImage im = ImageIO.read(file.getInputStream());
      int min = Math.min(im.getHeight(), im.getWidth());
      storage = uploadImage(file.getBytes(), min);
      storage.setIsStartAvatar(false);
      fileRepository.save(storage);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    StorageDTO storageDTO = new StorageDTO();
    storageDTO.setUrl(storage.getUrl());
    return CommonResponseDTO.<StorageDTO>builder().data(storageDTO).build();
  }

  public File uploadImage(byte[] bytes, int widthHeight) {
    try {
      Map<Object, Object> options = Map.of(
          "transformation", new Transformation<>()
              .gravity("auto")
              .width(widthHeight)
              .height(widthHeight)
              .crop("crop")
      );
      var uploadResult = cloudinary.uploader().upload(bytes, options);

      String publicId = (String) uploadResult.get("public_id");
      String secureUrl = (String) uploadResult.get("secure_url");
      String format = (String) uploadResult.get("format");

      return new File()
          .setPublicId(publicId)
          .setUrl(secureUrl)
          .setFormat(format);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Transactional
  public CommonResponseDTO<String> deleteImage(String publicId) {
    try {
      // Удаляем изображение из Cloudinary
      cloudinary.uploader().destroy(publicId, null);
      // Удаляем изображение из БД
      fileRepository.deleteByPublicId(publicId);
      return CommonResponseDTO.<String>builder().data("Image deleted successfully").build();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @SuppressWarnings("java:S2245")
  public String generateStartAvatar() {
    List<File> avatars = fileRepository.findAllByIsStartAvatar(true);
    if (avatars.size() < 2) {
      return null;
    }
    int index;
    synchronized (cloudinary) {
      index = ThreadLocalRandom.current().nextInt(0, avatars.size());
    }
    return avatars.get(index).getUrl();
  }
}
