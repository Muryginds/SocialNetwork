package ru.skillbox.zerone.backend;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(info = @Info(
    title = "Zerone Social Network API",
    version = "v1",
    description = "Документация по API"
))
public class ZeroneBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(ZeroneBackendApplication.class, args);
  }
}