package ru.skillbox.zerone.backend.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationUtils {
  public final String LETTERS_PATTERN = "^[A-zА-яЁё\\s-]{3,}$";
  public final String LETTERS_PATTERN_DESCRIPTION = "must have at least 3 characters";
  public final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9]).{8,}$";
  public final String PASSWORD_PATTERN_DESCRIPTION = "password must contain at least 1 uppercase, 1 lowercase, 1 special character and 1 digit";
  public final String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
  public final String EMAIL_PATTERN_DESCRIPTION = "email must match form aaa@bbb.cc";
  public final String SIZE_3_PATTERN_DESCRIPTION = "must have at least 3 characters";
  public final String SIZE_8_PATTERN_DESCRIPTION = "must have at least 8 characters";
  public final String MESSAGE_NOT_EMPTY_DESCRIPTION = "message must be not empty";
}