package ru.skillbox.zerone.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.skillbox.zerone.backend.exception.UserAlreadyExistException;
import ru.skillbox.zerone.backend.model.dto.response.ResponseDTO;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class ControllerAdvisor {

  @ExceptionHandler(UserAlreadyExistException.class)
  public ResponseEntity<Object> handleUserAlreadyExistException(UserAlreadyExistException exception) {

    var response = ResponseDTO.builder()
        .data(Map.of("Message", "ok"))
        .error(exception.getLocalizedMessage())
        .timestamp(LocalDateTime.now())
        .build();

    return ResponseEntity.ok(response);
  }
}