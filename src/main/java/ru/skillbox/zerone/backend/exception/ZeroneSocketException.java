package ru.skillbox.zerone.backend.exception;

public class ZeroneSocketException extends RuntimeException {
  public ZeroneSocketException(Exception e) {
    super(e);
  }
}
