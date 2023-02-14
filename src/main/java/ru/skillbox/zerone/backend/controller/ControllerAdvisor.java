package ru.skillbox.zerone.backend.controller;

import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.skillbox.zerone.backend.exception.RegistrationCompleteException;
import ru.skillbox.zerone.backend.exception.UserAlreadyExistException;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;

@ControllerAdvice
public class ControllerAdvisor {

  @ExceptionHandler({
      RegistrationCompleteException.class,
      ConstraintViolationException.class,
      BadCredentialsException.class,
      UserAlreadyExistException.class,
      JwtException.class
  })
  ResponseEntity<Object> handleException(Exception e) {
    var response = CommonResponseDTO.builder()
        .error(e.getLocalizedMessage())
        .build();

    return ResponseEntity.badRequest().body(response);
  }
}