package ru.skillbox.zerone.backend.configuration;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
public class DriveManager {

  @Value("${google-drive.mimetype.folder}")
  private String folderMimeType;

  @Value("${google-drive.space}")
  private String space;

  @Value("${google-drive.fields}")
  private String fields;

  @Value("${google-drive.fields_set}")
  private String fieldsSet;

  @Value("${google-drive.file_type}")
  private String fileType;

  @Value("${google-drive.index_of_first_element}")
  private int indexOfFirstElement;

  private final Drive drive;

  public File findFolderByName(String folderName) throws IOException {

    String pageToken = null;
    List<File> list = new ArrayList<>();
    FileList result;

    String query = " name = '" + folderName + "' "
        + " and mimeType = '" + folderMimeType + "' ";

    do {
      result = drive.files().list().setQ(query).setSpaces(space)
          .setFields(fields)
          .setPageToken(pageToken).execute();
      list.addAll(result.getFiles());

      pageToken = result.getNextPageToken();

    } while (pageToken != null);

    if (!list.isEmpty()) {

      return result.getFiles().get(indexOfFirstElement);
    }

    return null;
  }

  public File createFolder(String folderName) throws IOException {

    File fileMetadata = new File();

    fileMetadata.setName(folderName);
    fileMetadata.setMimeType(folderMimeType);

    return drive.files().create(fileMetadata).setFields(fieldsSet).execute();
  }


  public List<File> listFilesFromFolder(String folderId) throws IOException {

    if (folderId == null) {
      folderId = "root";
    }
    String query = "'" + folderId + "' in parents";

    FileList result = drive.files().list()
        .setQ(query)
        .setFields(fields)
        .execute();

    return result.getFiles();
  }

  public void uploadFile(java.io.File file, String folderId) throws IOException {

    File fileMetadata = new File();
    fileMetadata.setParents(Collections.singletonList(folderId));
    fileMetadata.setName(file.getName());

    FileContent mediaContent = new FileContent(fileType, file);

    drive.files().create(fileMetadata, mediaContent).execute();
  }

  public void deleteFile(File file) throws IOException {

    drive.files().delete(file.getId()).execute();
  }
}