package ru.skillbox.zerone.backend.tests;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.skillbox.zerone.backend.model.entity.Admin;
import ru.skillbox.zerone.backend.model.entity.Dialog;
import ru.skillbox.zerone.backend.model.entity.Message;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.repository.AdminRepository;
import ru.skillbox.zerone.backend.repository.DialogRepository;
import ru.skillbox.zerone.backend.repository.MessageRepository;
import ru.skillbox.zerone.backend.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EntityTests {
  private final UserRepository userRepository;
  private final AdminRepository adminRepository;
  private final DialogRepository dialogRepository;
  private final MessageRepository messageRepository;

  @Bean
  public ApplicationRunner Tests() {
    return args -> {
      userTest();
      adminTest();
      dialogTest();
      messageTest();
    };
  }

  private void userTest() {
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
    user = user;
  }

  private void adminTest() {
    Admin admin = Admin.builder()
        .name("Alex")
        .email("alex@mail.ru")
        .password("password")
//          .type(UserType.ADMIN)
        .build();
    adminRepository.saveAndFlush(admin);
  }

  private void dialogTest() {
    User user = userRepository.findById(1L).get();
    Dialog dialog = Dialog.builder()
        .sender(user)
        .recipient(user)
        .build();
    dialogRepository.saveAndFlush(dialog);
  }

  private void messageTest() {
    Dialog dialog = dialogRepository.findById(1L).get();
    Message message = Message.builder()
        .dialog(dialog)
        .messageText("Сообщение")
        .build();
    messageRepository.saveAndFlush(message);
    dialog = dialog;
  }
}
