package ru.skillbox.zerone.backend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;
import java.util.Date;

public class TokenDecoder {
  private String token;
  private String[] chunks;
  private ObjectMapper objectMapper;
  private Base64.Decoder decoder;
  private Payload payload;
  public TokenDecoder(String token) {
    this.token = token;
    objectMapper = new ObjectMapper();
    chunks = token.split("\\.");
    decoder = Base64.getUrlDecoder();
  }

  public String getSubject() throws JsonProcessingException {
    preparePayload();
    return payload.getSub();
  }

  public String[] roles() throws JsonProcessingException {
    preparePayload();
    return payload.getRoles();
  }

  public Date getCreatedAt() throws JsonProcessingException {
    preparePayload();
    return new Date(payload.getIat() * 1000L);
  }

  public Date getExpired() throws JsonProcessingException {
    preparePayload();
    return new Date(payload.exp * 1000L);
  }

  private void preparePayload() throws JsonProcessingException {
    if (payload == null) {
      String payloadJson = new String(decoder.decode(chunks[1]));
      payload = objectMapper.readValue(payloadJson, Payload.class);
    }
  }

  @Data
  @NoArgsConstructor
  private static class Payload {
    private String sub;
    private String[] roles;
    private int iat;
    private int exp;
  }
}
