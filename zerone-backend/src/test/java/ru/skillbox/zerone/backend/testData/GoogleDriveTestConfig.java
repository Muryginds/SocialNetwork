package ru.skillbox.zerone.backend.testData;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.testing.json.MockJsonFactory;
import com.google.api.services.drive.Drive;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class GoogleDriveTestConfig {

  @Bean
  @Primary
  public Drive drive() {
    Credential mockCredential = Mockito.mock(Credential.class);
    Mockito.when(mockCredential.getAccessToken()).thenReturn("valid_access_token");
    return new Drive.Builder(
        new NetHttpTransport(),
        new MockJsonFactory(),
        mockCredential)
        .setApplicationName("MyApplication")
        .build();
  }
}
