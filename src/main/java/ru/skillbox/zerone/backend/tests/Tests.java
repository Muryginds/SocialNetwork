package ru.skillbox.zerone.backend.tests;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.skillbox.zerone.backend.model.User;
import ru.skillbox.zerone.backend.model.enumerated.MessagePermissions;
import ru.skillbox.zerone.backend.model.enumerated.UserStatus;
import ru.skillbox.zerone.backend.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class Tests {
  private final UserRepository userRepository;

//  @Bean
  public ApplicationRunner userSaveAndFlushTest() {
    return args -> {
      User user = User.builder()
          .firstName("Vladimir")
          .lastName("Panfilov")
//          .regDate(LocalDateTime.now())
          .birthDate(LocalDate.now())
          .email("vrpanfilov@yandex.ru")
          .phone("+9051234567")
          .password("A password code")
//          .photo("SomeUri")
          .about("Что-то о себе")
//          .status(UserStatus.INACTIVE)
          .country("Россия")
          .city("Москва")
          .confirmationCode("Confirmation code")
//          .messagePermissions(MessagePermissions.ALL)
//          .isApproved(true)
          .lastOnlineTime(LocalDateTime.now())
//          .isBlocked(false)
//          .isDeleted(true)
          .build();

      userRepository.saveAndFlush(user);
    };
  }

}
