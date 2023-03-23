package ru.skillbox.zerone.backend.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.zerone.backend.model.entity.WebSocketConnection;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WebSocketConnectionRepository extends CrudRepository<WebSocketConnection, Long> {
  Optional<WebSocketConnection> findBySessionId(UUID sessionId);
  boolean existsBySessionId(UUID sessionId);
}
