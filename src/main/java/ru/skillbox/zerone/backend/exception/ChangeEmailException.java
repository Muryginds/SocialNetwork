package ru.skillbox.zerone.backend.exception;

public class ChangeEmailException extends ZeroneException {
  public ChangeEmailException(String confirmationCode, String email) {
    super(String.format("код подтверждения = %s не корректный или Email = %s не зарегистрирован", confirmationCode, email));
  }
}
