package ru.skillbox.zerone.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.zerone.admin.exception.StartAvatarException;
import ru.skillbox.zerone.admin.model.entity.File;
import ru.skillbox.zerone.admin.model.entity.User;
import ru.skillbox.zerone.admin.repository.FileRepository;
import ru.skillbox.zerone.admin.repository.UserRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AvatarService {

  private final StorageService storageService;
  private final UserRepository userRepository;
  private final FileRepository fileRepository;

  @Transactional
  public void updateStartAvatars(MultipartFile[] fileList) {
    deleteStartAvatars();
    List<File> files = saveStartAvatarsToCloudinary(fileList);
    fileRepository.saveAllAndFlush(files);
  }

  private List<File> saveStartAvatarsToCloudinary(MultipartFile[] fileList) {
    List<File> startAvatars = new ArrayList<>();
    for (MultipartFile file : fileList) {
      try {
        BufferedImage im = ImageIO.read(file.getInputStream());
        int min = Math.min(im.getHeight(), im.getWidth());
        File startAvatar = storageService.uploadImage(file.getBytes(), min);
        startAvatar.setIsStartAvatar(true);
        startAvatars.add(startAvatar);
      } catch (IOException e) {
        throw new StartAvatarException("Не обрабатывается файл " + file.getName());
      }
    }
    return startAvatars;
  }

  public void fillAvatarsForNullPhotos() {
    List<File> startAvatars = fileRepository.findAllByIsStartAvatar(true);
    if (startAvatars.isEmpty()) {
      return;
    }
    List<User> users = userRepository.findAllByPhotoNull();
    users.forEach(user -> {
      int index = ThreadLocalRandom.current().nextInt(0, startAvatars.size());
      user.setPhoto(startAvatars.get(index).getUrl());
    });
    userRepository.saveAll(users);
  }

  @Transactional
  public void deleteStartAvatars() {
    userRepository.clearPhotosWithStartAvatars();
    storageService.deleteFiles(true);
    fileRepository.deleteAllByIsStartAvatar(true);
  }

  @Transactional
  public void deleteAllAvatars() {
    userRepository.clearAllPhotos();
    storageService.deleteFiles(false);
    fileRepository.deleteAll();
  }
}
