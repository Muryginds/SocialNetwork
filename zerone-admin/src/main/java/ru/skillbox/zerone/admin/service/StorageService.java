package ru.skillbox.zerone.admin.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.admin.model.entity.File;
import ru.skillbox.zerone.admin.repository.FileRepository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class StorageService {
  private final Cloudinary cloudinary;
  private final FileRepository fileRepository;

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

      File file = new File()
          .setPublicId(publicId)
          .setUrl(secureUrl)
          .setFormat(format);
      fileRepository.save(file);

      return file;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void deleteFiles(boolean isStartAvatar) {
    List<File> files = isStartAvatar ?
        fileRepository.findAllByIsStartAvatar(isStartAvatar) :
        fileRepository.findAll();
    files.forEach(file -> {
      Map<Object, Object> options = Map.of();
      try {
        cloudinary.uploader().destroy(file.getPublicId(), options);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    });
  }
}
