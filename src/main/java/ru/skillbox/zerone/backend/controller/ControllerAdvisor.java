package ru.skillbox.zerone.backend.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.skillbox.zerone.backend.exception.BlacklistException;
import ru.skillbox.zerone.backend.exception.RegistrationCompleteException;
import ru.skillbox.zerone.backend.exception.UserAlreadyExistException;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;

@ControllerAdvice
public class ControllerAdvisor {

  @ExceptionHandler(Exception.class)
  ResponseEntity<Object> handleException(Exception e) {

    if (e instanceof RegistrationCompleteException ||
    e instanceof ConstraintViolationException ||
    e instanceof BadCredentialsException ||
    e instanceof UserAlreadyExistException ||
    e instanceof BlacklistException ||
    e instanceof UsernameNotFoundException) {

      var response = CommonResponseDTO.builder()
          .error(e.getLocalizedMessage())
          .build();

      return ResponseEntity.badRequest().body(response);
    }
    e.printStackTrace();
    return ResponseEntity.internalServerError().body(null);

  }
}