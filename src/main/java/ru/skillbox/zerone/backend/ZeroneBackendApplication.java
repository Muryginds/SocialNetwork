package ru.skillbox.zerone.backend;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.skillbox.zerone.backend.model.enumerated.MessagePermissions;
import ru.skillbox.zerone.backend.model.enumerated.UserStatus;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
public class ZeroneBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(ZeroneBackendApplication.class, args);
  }

}
