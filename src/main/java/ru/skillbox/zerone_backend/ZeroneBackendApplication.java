package ru.skillbox.zerone_backend;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.skillbox.zerone_backend.enumerated.MessagePermissions;
import ru.skillbox.zerone_backend.enumerated.UserStatus;
import ru.skillbox.zerone_backend.model.User;
import ru.skillbox.zerone_backend.repository.UserRepository;

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
      User user = new User();
      user.setFirstName("Vladimir");
      user.setLastName("Panfilov");
      user.setRegDate(LocalDateTime.now());
      user.setBirthDate(LocalDate.now());
      user.setEmail("vrpanfilov@yandex.ru");
      user.setPhone("+9051234567");
      user.setPassword("A password code");
//			user.setPhoto("SomeUri");
      user.setAbout("Что-то о себе");
      user.setStatus(UserStatus.ACTIVE);
      user.setCountry("Россия");
      user.setCity("Москва");
      user.setConfirmationCode("Confirmation code");
      user.setApproved(true);
      user.setMessagePermissions(MessagePermissions.FRIENDS);
      user.setLastOnlineTime(LocalDateTime.now());
      user.setBlocked(false);
      user.setDeleted(true);

      userRepository.saveAndFlush(user);
    };
  }
}
