package ru.skillbox.zerone.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class BlacklistException extends RuntimeException {
  public BlacklistException(String cause) {
    super(cause);
  }
}
