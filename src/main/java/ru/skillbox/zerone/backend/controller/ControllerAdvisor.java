package ru.skillbox.zerone.backend.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.skillbox.zerone.backend.exception.BlacklistException;
import ru.skillbox.zerone.backend.exception.RegistrationCompleteException;
import ru.skillbox.zerone.backend.exception.UserAlreadyExistException;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;

@ControllerAdvice
public class ControllerAdvisor {

  @ExceptionHandler({
      Exception.class
//      RegistrationCompleteException.class,
//      ConstraintViolationException.class,
//      BadCredentialsException.class,
//      UserAlreadyExistException.class,
//      BlacklistException.class
  })
  ResponseEntity<Object> handleException(Exception e) {
    var response = CommonResponseDTO.builder()
        .error(e.getLocalizedMessage())
        .build();

    return ResponseEntity.badRequest().body(response);
  }
}