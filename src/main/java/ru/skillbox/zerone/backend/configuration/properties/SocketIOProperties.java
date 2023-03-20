package ru.skillbox.zerone.backend.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "websocket")
public class SocketIOProperties {
  private String host;
  private Integer port;
}
