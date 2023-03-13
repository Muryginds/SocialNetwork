package ru.skillbox.zerone.backend.exception;

public class CommentNotFoundException extends ZeroneException{

  public CommentNotFoundException(String cause) {
    super(cause);
  }
}
