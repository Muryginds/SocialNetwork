package ru.skillbox.zerone.backend.testData;

import ru.skillbox.zerone.backend.model.entity.Role;
import ru.skillbox.zerone.backend.model.entity.User;

import java.util.List;

public interface UserMockUtils {
  String USER_ROLE = "ROLE_USER";

  default User getTestUser(String email, String password) {
    return User.builder()
        .email(email)
        .password(password)
        .isApproved(true)
        .confirmationCode("test")
        .roles(List.of(new Role(1L, USER_ROLE)))
        .build();
  }
}
