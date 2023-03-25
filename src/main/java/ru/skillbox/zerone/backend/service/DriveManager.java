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

@Service
@RequiredArgsConstructor
public class DriveManager {

  private final GoogleDriveProperties googleDriveProperties;
  private final Drive drive;
  private String query;

  @PostConstruct
  private void query() {
    query = String.format(" name = '%s' "
        + " and mimeType = '%s' ", googleDriveProperties.getFolderName(), googleDriveProperties.getMimeType());
  }

  public File findFolderByName() throws IOException {

    String pageToken = null;
    List<File> list = new ArrayList<>();
    FileList result;

    do {
      result = drive.files().list().setQ(query).setSpaces(googleDriveProperties.getSpace())
          .setFields(googleDriveProperties.getFields())
          .setPageToken(pageToken).execute();
      list.addAll(result.getFiles());

      pageToken = result.getNextPageToken();

    } while (pageToken != null);

    if (!list.isEmpty()) {

      return result.getFiles().get(googleDriveProperties.getIndexOfFirstElement());
    }

    return null;
  }

  public File createFolder(String folderName) throws IOException {

    File fileMetadata = new File();

    fileMetadata.setName(folderName);
    fileMetadata.setMimeType(googleDriveProperties.getMimeType());

    return drive.files().create(fileMetadata).setFields(googleDriveProperties.getFieldsSet()).execute();
  }

  public List<File> listFilesFromFolder(String folderId) throws IOException {

    if (folderId == null) {
      folderId = "root";
    }
    String listFilesQuery = String.format("'%s' in parents", folderId);

    FileList result = drive.files().list()
        .setQ(listFilesQuery)
        .setFields(googleDriveProperties.getFields())
        .execute();

    return result.getFiles();
  }

  public void uploadFile(java.io.File file, String folderId) throws IOException {

    File fileMetadata = new File();
    fileMetadata.setParents(Collections.singletonList(folderId));
    fileMetadata.setName(file.getName());

    FileContent mediaContent = new FileContent(googleDriveProperties.getFileType(), file);

    drive.files().create(fileMetadata, mediaContent).execute();
  }

  public void deleteFile(File file) throws IOException {

    drive.files().delete(file.getId()).execute();
  }
}
