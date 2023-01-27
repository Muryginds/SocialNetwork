package ru.skillbox.zeronebackend;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.skillbox.zeronebackend.model.entity.User;
import ru.skillbox.zeronebackend.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
public class ZeroneBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(ZeroneBackendApplication.class, args);
  }


  private final UserRepository userRepository;

  public ZeroneBackendApplication(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Bean
  public ApplicationRunner init() {
    return args -> {
      User user = User.builder()
          .firstName("Vladimir")
          .lastName("Panfilov")
          .regDate(LocalDateTime.now())
          .birthDate(LocalDate.now())
          .email("vrpanfilov@yandex.ru")
          .phone("+9051234567")
          .password("A password code")
//          .photo("SomeUri")
          .about("Что-то о себе")
//          .status(UserStatus.ACTIVE)
          .country("Россия")
          .city("Москва")
          .confirmationCode("Confirmation code")
//          .messagePermissions(MessagePermissions.FRIENDS)
//          .isApproved(true)
          .lastOnlineTime(LocalDateTime.now())
//          .isBlocked(false)
//          .isDeleted(true)
          .build();

      userRepository.saveAndFlush(user);
    };
  }
}
