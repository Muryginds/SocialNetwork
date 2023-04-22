package ru.skillbox.zerone.backend.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "vk-api")
@Getter
@Setter
public class VKWebClientProperties {
  private String accessToken;
  private String rootUrl;
  private String version;
  private String findCountriesMethodUri;
  private String findCitiesMethodUri;
}
