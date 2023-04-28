package ru.skillbox.zerone.backend.exception;

public class PostNotFoundException extends ZeroneException {


  public PostNotFoundException(String cause) {
    super(cause);
  }

  public PostNotFoundException(long id) {
    super(String.format("Пост с id=%d не найден", id));
  }
}
