package ru.skillbox.zerone.backend.service;

import lombok.Synchronized;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.BlacklistException;
import ru.skillbox.zerone.backend.model.entity.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class BlacklistService {

  private final Map<String, String> loggedUsers = new HashMap<>();
  private final Map<String, Date> blacklist = new HashMap<>();

  public void processLogin(String email, String token) {
    synchronized (this) {
      if (loggedUsers.containsKey(email)) {
        blacklist.put(token, new Date());
      }
      loggedUsers.put(email, token);
    }
  }

  public void processLogout(User user) {
    synchronized (this) {
      String token = loggedUsers.get(user.getEmail());
      if (token != null) {
        blacklist.put(token, new Date());
        loggedUsers.remove(user.getEmail());
      }
    }
  }

  public boolean validateToken(String token) {
    synchronized (this) {
      if (blacklist.containsKey(token) || !loggedUsers.containsValue(token)) {
        throw new BlacklistException(token);
      }
      return true;
    }
  }
}

