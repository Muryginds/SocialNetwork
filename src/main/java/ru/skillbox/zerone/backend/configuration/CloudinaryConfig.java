package ru.skillbox.zerone.backend.configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
  @Value("zeroneproject")
  private String cloudName;
  @Value("551572451498292")
  private String apiKey;
  @Value("bmOA13-uV_37Hm9Vfu4E_heObw4")
  private String apiSecret;
  @Bean
  public Cloudinary cloudinary() {
    return new Cloudinary(ObjectUtils.asMap(
        "cloud_name", cloudName,
        "api_key", apiKey,
        "api_secret", apiSecret));
  }
}