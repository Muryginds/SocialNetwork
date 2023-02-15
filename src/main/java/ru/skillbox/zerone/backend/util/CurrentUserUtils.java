package ru.skillbox.zerone.backend.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.skillbox.zerone.backend.model.entity.User;

@UtilityClass
public class CurrentUserUtils {
  public User getCurrentUser() {
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }
}
