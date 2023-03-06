package ru.skillbox.zerone.backend.exception;

public class UserAlreadyExistException extends ZeroneException {
  public UserAlreadyExistException(String email) {
    super(String.format("Пользователь с email: %s уже существует", email));
  }
}