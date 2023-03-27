package ru.skillbox.zerone.backend.service;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.configuration.properties.GoogleDriveProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class DriveManager {

  private final GoogleDriveProperties driveProperties;
  private final Drive drive;
  private String query;

  @PostConstruct
  private void query() {
    query = String.format(" name = '%s' "
        + " and mimeType = '%s' ", driveProperties.getFolderName(), driveProperties.getMimeType());
  }

  public Optional<File> findFolderByName() throws IOException {

    String pageToken = null;
    List<File> list = new ArrayList<>();
    FileList result;

    do {
      result = drive.files().list().setQ(query).setSpaces(driveProperties.getSpace())
          .setFields(driveProperties.getFields())
          .setPageToken(pageToken).execute();
      list.addAll(result.getFiles());

      pageToken = result.getNextPageToken();

    } while (pageToken != null);

    if (!list.isEmpty()) {

      return Optional.ofNullable(result.getFiles().get(driveProperties.getIndexOfFirstElement()));
    }

    return Optional.empty();
  }

  public File createFolder(String folderName) throws IOException {

    File fileMetadata = new File();

    fileMetadata.setName(folderName);
    fileMetadata.setMimeType(driveProperties.getMimeType());

    return drive.files().create(fileMetadata).setFields(driveProperties.getFieldsSet()).execute();
  }


  public List<File> listFilesFromFolder(String folderId) throws IOException {

    if (folderId == null) {
      folderId = "root";
    }
    String listFilesQuery = String.format("'%s' in parents", folderId);

    FileList result = drive.files().list()
        .setQ(listFilesQuery)
        .setFields(driveProperties.getFields())
        .execute();

    return result.getFiles();
  }

  public void uploadFile(java.io.File file, String folderId) throws IOException {

    File fileMetadata = new File();
    fileMetadata.setParents(Collections.singletonList(folderId));
    fileMetadata.setName(file.getName());

    FileContent mediaContent = new FileContent(driveProperties.getFileType(), file);

    drive.files().create(fileMetadata, mediaContent).execute();
  }

  public void deleteFile(File file) throws IOException {

    drive.files().delete(file.getId()).execute();
  }
}