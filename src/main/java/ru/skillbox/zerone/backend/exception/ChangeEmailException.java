package ru.skillbox.zerone.backend.exception;

public class ChangeEmailException extends ZeroneException {
  public ChangeEmailException(String confirmationCode, String email) {
    super(String.format("код подтверждения = %s некорректный или Email = %s не зарегистрирован", confirmationCode, email));
  }
}
