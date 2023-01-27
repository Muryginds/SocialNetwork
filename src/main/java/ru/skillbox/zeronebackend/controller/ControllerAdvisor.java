package ru.skillbox.zeronebackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.skillbox.zeronebackend.exception.UserAlreadyExistException;
import ru.skillbox.zeronebackend.model.dto.response.ResponseDTO;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class ControllerAdvisor {

  @ExceptionHandler(UserAlreadyExistException.class)
  public ResponseEntity<Object> handleUserAlreadyExistException(
      UserAlreadyExistException exception, HttpServletRequest request) {

    var response = ResponseDTO.builder()
        .data(Map.of("Message", "ok"))
        .error(String.format(String.format("User with email: %s already exists", request.getParameter("email"))))
        .timestamp(LocalDateTime.now())
        .build();

    return ResponseEntity.ok(response);
  }
}