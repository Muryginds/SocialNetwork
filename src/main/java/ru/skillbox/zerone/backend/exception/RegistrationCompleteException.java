package ru.skillbox.zerone.backend.exception;

public class RegistrationCompleteException extends RuntimeException {

  public RegistrationCompleteException() {
    super("Registration was not completed");
  }
}