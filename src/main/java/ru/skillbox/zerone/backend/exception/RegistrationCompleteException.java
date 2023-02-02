package ru.skillbox.zerone.backend.exception;

public class RegistrationCompleteException extends RuntimeException {

  public RegistrationCompleteException(String cause) {
    super(String.format("Registration was not completed: %s", cause));
  }
}