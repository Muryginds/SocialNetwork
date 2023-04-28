package ru.skillbox.zerone.backend.exception;

public class CommentNotFoundException extends ZeroneException {

  public CommentNotFoundException(String cause) {
    super(cause);
  }

  public CommentNotFoundException(long id) {
    super(String.format("Коммент с id=%d не найден", id));
  }
}
