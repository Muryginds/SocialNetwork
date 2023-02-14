package ru.skillbox.zerone.backend.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class MailServiceLoggingAspect {

  @Pointcut("execution(* sendVerificationEmail(..))")
  public void sendVerificationEmailPointcut(){}

  @AfterReturning(value = "args(email, verifyCode) && sendVerificationEmailPointcut()", argNames = "email,verifyCode")
  public void sendVerificationEmailAdvice(String email, String verifyCode){
    log.debug("IN sendVerificationEmail - user with username: {} mail sent successfully", email);
  }
}
