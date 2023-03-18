package ru.skillbox.zerone.backend.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public class SocketIORepository {

  private final HashMap<String, String> tokenEmail = new HashMap<>();
  private final HashMap<String, String> sessionIdEmail = new HashMap<>();

  public void saveTokenEmail(String token, String email) {
    tokenEmail.put(token, email);
  }

  public String findEmailByToken(String token) {
    return tokenEmail.get(token);
  }

  public void saveSessionIdEmail(String sessionId, String email) {
    sessionIdEmail.put(sessionId, email);
  }
}
