package ru.skillbox.zerone.backend.exception;

public class ChangeEmailException extends ZeroneException {
  public ChangeEmailException(String confirmationCode, String email) {
    super(String.format("confirmationCode = %s dont correct or Email = %s not registered", confirmationCode, email));
  }
}
