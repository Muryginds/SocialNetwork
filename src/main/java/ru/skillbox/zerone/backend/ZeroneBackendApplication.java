package ru.skillbox.zerone.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ZeroneBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(ZeroneBackendApplication.class, args);
  }
}