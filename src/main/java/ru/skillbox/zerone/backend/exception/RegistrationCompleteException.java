package ru.skillbox.zerone.backend.exception;

public class RegistrationCompleteException extends ZeroneException {
  public RegistrationCompleteException(String cause) {
    super(String.format("Регистрация не была завершена: %s", cause));
  }
}