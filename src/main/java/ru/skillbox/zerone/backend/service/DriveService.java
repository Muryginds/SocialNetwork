package ru.skillbox.zerone.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.skillbox.zerone.backend.configuration.DriveManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriveService {

  @Value("${output.path}")
  private String path;

  @Value("${google-drive.months_to_subtract}")
  private int monthsToSubtract;

  private String folderId;

  private final DriveManager driveManager;


  @Scheduled(cron = "${cron.expression}")
  public void transferLogsToGoogleDrive() throws IOException {

    com.google.api.services.drive.model.File folderFromDrive = driveManager.findFolderByName(path);

    folderId = (folderFromDrive == null) ? driveManager.createFolder(path).getId()
        : driveManager.findFolderByName(path).getId();

    List<com.google.api.services.drive.model.File> filesFromGoogleDrive = driveManager.listFilesFromFolder(folderId);
    List<String> gdFilenames = filesFromGoogleDrive.stream().map(com.google.api.services.drive.model.File::getName).toList();

    java.io.File dir = new java.io.File(path);
    java.io.File[] logFiles = dir.listFiles();

    Assert.notNull(logFiles, "Not null!");

    for (java.io.File logFile : logFiles) {

      if (gdFilenames.contains(logFile.getName())) {

        checkFilesAndTransferIfLogfileIsBigger(logFile, filesFromGoogleDrive, folderId);

      } else {
        driveManager.uploadFile(logFile, folderId);
      }

      if (!logFile.equals(logFiles[logFiles.length - 1])) {

        Files.delete(Paths.get(logFile.getPath()));
      }
    }
    deleteOldLogsInDrive();
  }

  private void checkFilesAndTransferIfLogfileIsBigger(java.io.File logFile, List<com.google.api.services.drive.model.File> filesFromDrive, String folderId) throws IOException {

    for (com.google.api.services.drive.model.File file : filesFromDrive) {

      if (logFile.getName().equals(file.getName()) && logFile.length() > file.getSize()) {

        driveManager.deleteFile(file);
        driveManager.uploadFile(logFile, folderId);
      }
    }
  }

  private void deleteOldLogsInDrive() throws IOException {

    List<com.google.api.services.drive.model.File> filesFromGoogleDrive = driveManager.listFilesFromFolder(folderId);

    for (com.google.api.services.drive.model.File file : filesFromGoogleDrive) {

      LocalDate createdDate = new java.sql.Date(file.getCreatedTime().getValue()).toLocalDate();

      if (createdDate.isBefore(LocalDate.now().minusMonths(monthsToSubtract))) {
        driveManager.deleteFile(file);
      }
    }
  }
}