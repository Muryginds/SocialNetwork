package ru.skillbox.zerone.backend.exception;

public class UserNotFoundException extends ZeroneException {
  public UserNotFoundException(String cause) {
    super(String.format("User with email: %s not found", cause));
  }

  public UserNotFoundException(long id) {
    super(String.format("Пользователь с id %s не найден", id));
  }
}
