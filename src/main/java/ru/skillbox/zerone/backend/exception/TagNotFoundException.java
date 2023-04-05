package ru.skillbox.zerone.backend.exception;

public class TagNotFoundException extends ZeroneException {
    public TagNotFoundException(long id) {
    super(String.format("Тэг с id %s не найден", id));
  }
}





