package ru.skillbox.zerone_backend.model.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String code;
    private String passwd1;
    private String email;
    private String passwd2;
}