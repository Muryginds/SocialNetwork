package ru.skillbox.zerone.backend.util;

public final class ValidationUtils {
  public static final String LETTERS_PATTERN = "^[A-zА-яЁё\\s-]{2,}$";
  public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9]).{6,}$";
  public static final String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
  private ValidationUtils() {}
}