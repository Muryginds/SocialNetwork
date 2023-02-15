package ru.skillbox.zerone.backend.exception;

public class BlacklistException extends RuntimeException {
  public BlacklistException(String token) {
    super(String.format("Token " + token + " is in BlackList"));
  }
}
