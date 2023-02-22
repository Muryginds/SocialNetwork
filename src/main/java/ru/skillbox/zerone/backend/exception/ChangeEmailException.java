package ru.skillbox.zerone.backend.exception;

public class ChangeEmailException extends RuntimeException {
  public ChangeEmailException(String cause) {
    super(String.format("Email change was not completed: %s", cause));
  }
}
