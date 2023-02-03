package ru.skillbox.zerone.backend.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ApiException {
  private final String message;
  private final Throwable throwable;
  private final HttpStatus httpStatus;
  private final LocalDateTime localDateTime;

}
