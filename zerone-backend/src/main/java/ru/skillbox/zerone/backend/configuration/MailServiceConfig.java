package ru.skillbox.zerone.backend.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mail-service")
@Getter
@Setter
public class MailServiceConfig {
  private String serverAddress;
  private String frontAddress;
}
