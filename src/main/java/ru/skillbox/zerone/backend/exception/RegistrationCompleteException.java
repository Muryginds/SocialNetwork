package ru.skillbox.zerone.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Email was not found")
public class RegistrationCompleteException extends RuntimeException {
  public RegistrationCompleteException() {
    super("Registration was not completed");
  }
}