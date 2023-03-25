package ru.skillbox.zerone.backend.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.zerone.backend.model.entity.WebSocketConnection;

import java.util.List;
import java.util.UUID;

@Repository
public interface WebSocketConnectionRepository extends CrudRepository<WebSocketConnection, UUID> {
  List<WebSocketConnection> findAllByUserId(String userId);
  boolean existsByUserId(String userId);
}
