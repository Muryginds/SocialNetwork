package ru.skillbox.zerone.backend.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.skillbox.zerone.backend.model.dto.request.AuthRequestDTO;

@Slf4j
@Aspect
@Component
public class LoginServiceLoggingAspect {

  @Pointcut("execution(* ru.skillbox.zerone.backend.service.LoginService.login(ru.skillbox.zerone.backend.model.dto.request.AuthRequestDTO))")
  public void loginPointcut(){}

  @AfterReturning("args(requestDTO) && loginPointcut()")
  public void loginAdvice(AuthRequestDTO requestDTO){
    log.debug("IN login - user with username: {} logged in successfully", requestDTO.getEmail());
  }
}
