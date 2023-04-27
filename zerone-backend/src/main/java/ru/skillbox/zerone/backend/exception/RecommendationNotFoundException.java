package ru.skillbox.zerone.backend.exception;

public class RecommendationNotFoundException extends ZeroneException {
  public RecommendationNotFoundException(Long id) {
    super(String.format("Рекомендованные друзья для пользователя с id %s не найдены", id));
  }
}
