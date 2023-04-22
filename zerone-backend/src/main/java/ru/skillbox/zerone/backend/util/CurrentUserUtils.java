package ru.skillbox.zerone.backend.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.skillbox.zerone.backend.model.entity.User;

import static java.lang.Boolean.TRUE;

@UtilityClass
public class CurrentUserUtils {
  public User getCurrentUser() {
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  public void checkUserIsNotRestricted(User user) {
    if (TRUE.equals(user.getIsDeleted())) {
      throw new LockedException("Учетная запись удалена");
    }
    if (TRUE.equals(user.getIsBlocked())) {
      throw new LockedException("Учетная запись заблокирована");
    }
  }
}
