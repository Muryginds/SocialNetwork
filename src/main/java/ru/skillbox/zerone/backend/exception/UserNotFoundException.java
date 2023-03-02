package ru.skillbox.zerone.backend.exception;

public class UserNotFoundException extends ZeroneException {
  public UserNotFoundException(String cause) {
    super(String.format("User was not found: %s", cause));
  }
}
