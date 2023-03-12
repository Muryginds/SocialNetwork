package ru.skillbox.zerone.backend.exception;

public class UserNotFoundException extends ZeroneException {
  public UserNotFoundException(String email) {
    super(String.format("Пользователь с почтой: %s не найден", email));
  }

  public UserNotFoundException(long id) {
    super(String.format("Пользователь с id %s не найден", id));
  }
}
