package ru.skillbox.zerone.backend.configuration;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableScheduling
public class GoogleDriveConfig {

  @Value("${google-drive.port}")
  private int port;
  @Value("${google-drive.authorize}")
  private String authorize;

  @Value("${google-drive.access_type}")
  private String accessType;

  @Value("${google-drive.app_name}")
  private String applicationName;

  @Value("${google-drive.tokens_path}")
  private String tokensDirectoryPath;

  @Value("${google-drive.credentials_path}")
  private String credentialsFilePath;


  @Bean
  public JsonFactory jsonFactory() {
    return GsonFactory.getDefaultInstance();
  }

  @Bean
  public NetHttpTransport httpTransport() throws GeneralSecurityException, IOException {
    return GoogleNetHttpTransport.newTrustedTransport();
  }

  @Bean
  public Credential credentials(NetHttpTransport httpTransport, JsonFactory jsonFactory) throws IOException {

    InputStream inputStream = GoogleDriveConfig.class.getResourceAsStream(credentialsFilePath);
    if (inputStream == null) {
      throw new FileNotFoundException("Resource not found: " + credentialsFilePath);
    }

    GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(jsonFactory, new InputStreamReader(inputStream));

    List<String> scopes = Collections.singletonList(DriveScopes.DRIVE);

    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, jsonFactory, clientSecrets, scopes)
        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(tokensDirectoryPath)))
        .setAccessType(accessType)
        .build();
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(port).build();

    return new AuthorizationCodeInstalledApp(flow, receiver).authorize(authorize);
  }


  @Bean
  public Drive instance(JsonFactory jsonFactory, Credential credential, HttpTransport httpTransport) {

    return new Drive.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName(applicationName)
        .build();
  }
}