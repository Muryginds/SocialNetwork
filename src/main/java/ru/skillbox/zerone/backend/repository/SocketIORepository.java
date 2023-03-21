package ru.skillbox.zerone.backend.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SocketIORepository {
  private final HashMap<Long, UUID> sessionContainer = new HashMap<>();

  public void saveSession(Long userId, UUID sessionId) {
    sessionContainer.put(userId, sessionId);
  }

  public Optional<UUID> findSessionByUserId(Long userId) {
    return Optional.ofNullable(sessionContainer.get(userId));
  }

  public boolean checkSessionIsActive(UUID uuid) {
    return sessionContainer.containsValue(uuid);
  }

  public void deleteByUUID(UUID uuid) {
    var iterator = sessionContainer.entrySet().iterator();
    while (iterator.hasNext()) {
      var entry = iterator.next();
      if (entry.getValue().equals(uuid)) {
        iterator.remove();
        return;
      }
    }
  }
}
