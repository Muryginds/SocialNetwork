package ru.skillbox.zerone.backend.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class JpaUserDetailsLoggingAspect {

  @Pointcut("execution(* ru.skillbox.zerone.backend.service.JpaUserDetails.loadUserByUsername(String))")
  public void loadUserByUsernamePointcut(){}

  @AfterReturning("args(username) && loadUserByUsernamePointcut()")
  public void loadUserByUsernameAdvice(String username){
    log.debug("IN loadUserByUsername - user with email: {} successfully loaded", username);
  }
}
