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

  @Pointcut("execution(* ru.skillbox.zerone.backend.service.MailService.sendVerificationEmail(..))")
  public void sendVerificationEmailPointcut(){}

  @Pointcut("execution(* ru.skillbox.zerone.backend.service.MailService.sendVerificationChangeEmail(..))")
  public void sendVerificationChangeEmailPointcut(){}

  @AfterReturning(value = "args(email, verifyCode) && sendVerificationEmailPointcut()", argNames = "email, verifyCode")
  public void sendVerificationEmailAdvice(String email, String verifyCode){
    log.debug("IN sendVerificationEmail - user with username: {} mail sent successfully", email);
  }

  @AfterReturning(value = "args(email, verifyCode) && sendVerificationChangeEmailPointcut()", argNames = "email, verifyCode")
  public void sendVerificationChangeEmailAdvice(String email, String verifyCode){
    log.debug("IN sendVerificationChangeEmail - user with username: {} mail sent successfully", email);
  }
}
