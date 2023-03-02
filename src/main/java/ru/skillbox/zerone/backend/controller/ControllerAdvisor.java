package ru.skillbox.zerone.backend.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.skillbox.zerone.backend.exception.ZeroneException;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;

@ControllerAdvice
public class ControllerAdvisor {

  @ExceptionHandler({
      ConstraintViolationException.class,
      ZeroneException.class
  })
  public ResponseEntity<Object> handleCustomExceptions(Exception e) {

    return ResponseEntity.badRequest().body(getResponse(e));
  }


  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleException(Exception e) {

    e.printStackTrace();
    return ResponseEntity.internalServerError().body(getResponse(e));
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<Object> handleBadCredentialsException(Exception e) {

    return ResponseEntity.status(401).body(getResponse(e));
  }

  private CommonResponseDTO<Object> getResponse(Exception e) {
    return CommonResponseDTO.builder()
        .error(e.getLocalizedMessage())
        .build();
  }
}