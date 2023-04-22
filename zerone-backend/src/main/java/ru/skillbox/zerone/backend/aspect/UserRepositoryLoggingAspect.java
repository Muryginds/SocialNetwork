package ru.skillbox.zerone.backend.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.skillbox.zerone.backend.model.entity.User;

@Slf4j
@Aspect
@Component
public class UserRepositoryLoggingAspect {

  @Pointcut(value = "execution(* org.springframework.data.jpa.repository.JpaRepository+.save(..))")
  public void saveUserPointcut() {}

  @AfterReturning("args(user) && saveUserPointcut()")
  public void saveUserAdvice(User user) {
    log.debug("IN save - user with username: {} successfully saved", user.getEmail());
  }

  @Pointcut(value = "execution(* ru.skillbox.zerone.backend.repository.UserRepository.findUserByEmail(*))")
  public void findUserByEmailPointcut() {}

  @AfterReturning("args(email) && findUserByEmailPointcut()")
  public void findUserByEmailAdvice(String email) {
    log.debug("IN findUserByEmailAdvice - attempted to find user with username: {}", email);
  }
}
