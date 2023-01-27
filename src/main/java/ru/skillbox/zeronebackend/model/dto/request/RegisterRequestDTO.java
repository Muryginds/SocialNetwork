package ru.skillbox.zeronebackend.model.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class RegisterRequestDTO {
  private String firstName;
  private String lastName;
  private String passwd1;
  private String email;
  private String passwd2;
}