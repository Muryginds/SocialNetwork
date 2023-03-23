package ru.skillbox.zerone.backend.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public class SocketIORepository {

  private final HashMap<String, String> sessionIdEmail = new HashMap<>();

  public void saveSessionIdEmail(String sessionId, String email) {
    sessionIdEmail.put(sessionId, email);
  }

  public String findEmailBySessionId(String sessionId) {
    return sessionIdEmail.get(sessionId);
  }

}
