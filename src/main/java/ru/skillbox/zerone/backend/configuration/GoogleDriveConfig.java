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
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.skillbox.zerone.backend.configuration.properties.GoogleDriveProperties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class GoogleDriveConfig {

  private final GoogleDriveProperties googleDriveProperties;

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

    InputStream inputStream = GoogleDriveConfig.class.getResourceAsStream(googleDriveProperties.getCredentialsPath());
    if (inputStream == null) {
      throw new FileNotFoundException("Resource not found: " + googleDriveProperties.getCredentialsPath());
    }

    GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(jsonFactory, new InputStreamReader(inputStream));

    List<String> scopes = Collections.singletonList(DriveScopes.DRIVE);

    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, jsonFactory, clientSecrets, scopes)
        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(googleDriveProperties.getTokensPath())))
        .setAccessType(googleDriveProperties.getAccessType())
        .build();
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(googleDriveProperties.getPort()).build();

    return new AuthorizationCodeInstalledApp(flow, receiver).authorize(googleDriveProperties.getAuthorize());
  }


  @Bean
  public Drive instance(JsonFactory jsonFactory, Credential credential, HttpTransport httpTransport) {

    return new Drive.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName(googleDriveProperties.getAppName())
        .build();
  }
}