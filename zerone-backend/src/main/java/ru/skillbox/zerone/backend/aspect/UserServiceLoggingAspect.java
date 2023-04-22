package ru.skillbox.zerone.backend.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;
import ru.skillbox.zerone.backend.model.dto.request.RegisterConfirmRequestDTO;
import ru.skillbox.zerone.backend.model.dto.request.RegisterRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;

@Slf4j
@Aspect
@Component
public class UserServiceLoggingAspect {

  @Pointcut("execution(* ru.skillbox.zerone.backend.service.UserService.registerAccount(ru.skillbox.zerone.backend.model.dto.request.RegisterRequestDTO))")
  public void registerAccountPointcut(){}

  @AfterReturning("args(request) && registerAccountPointcut()")
  public void registerAccountAdvice(RegisterRequestDTO request){
    log.debug("IN registerAccount - user with username: {} successfully registered", request.getEmail());
  }

  @Pointcut("execution(* ru.skillbox.zerone.backend.service.UserService.registrationConfirm(ru.skillbox.zerone.backend.model.dto.request.RegisterConfirmRequestDTO))")
  public void registrationConfirmPointcut(){}

  @AfterReturning("args(request) && registrationConfirmPointcut()")
  public void registrationConfirmAdvice(RegisterConfirmRequestDTO request){
    log.debug("IN registrationConfirm - user with username: {} successfully confirmed registration", request.getEmail());
  }

  @Pointcut("execution(* ru.skillbox.zerone.backend.service.UserService.getCurrentUser())")
  public void getCurrentUserPointcut(){}

  @AfterReturning(value = "getCurrentUserPointcut()", returning = "response")
  public void getCurrentUserAdvice(CommonResponseDTO<UserDTO> response){
    log.debug("IN getCurrentUser - user with username: {} successfully loaded", response.getData().getEmail());
  }
}
