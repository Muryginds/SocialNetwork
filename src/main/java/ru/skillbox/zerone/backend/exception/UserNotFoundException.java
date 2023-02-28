package ru.skillbox.zerone.backend.exception;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String cause) {
    super(String.format("User was not found: %s", cause));
  }
}
