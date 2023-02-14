package ru.skillbox.zerone.backend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


  @Data
  public class ChangePasswordTokenDto {
    private String token;
    private String password;
  }