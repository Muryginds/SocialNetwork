package ru.skillbox.zerone.backend.exception;


import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {

    public JwtAuthenticationException(String error) {
        super(error);
    }
}
