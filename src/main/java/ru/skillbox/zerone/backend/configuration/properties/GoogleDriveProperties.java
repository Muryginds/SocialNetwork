package ru.skillbox.zerone.backend.configuration.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "google-drive")
@Getter
@Setter
public class GoogleDriveProperties {

  private int port;
  private String authorize;
  private String accessType;
  private String appName;
  private String tokensPath;
  private String credentialsPath;
  private String mimeType;
  private String space;
  private String fields;
  private String fieldsSet;
  private String fileType;
  private int indexOfFirstElement;
  private String folderName;
  private int monthsToSubtract;

}
