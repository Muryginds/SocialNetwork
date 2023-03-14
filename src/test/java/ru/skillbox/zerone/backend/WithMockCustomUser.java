package ru.skillbox.zerone.backend;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
  String email() default "test@email.com";
  String lastName() default "LastName";
  String firstName() default "FirstName";
}
