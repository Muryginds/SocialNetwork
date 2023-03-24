package ru.skillbox.zerone.backend.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.skillbox.zerone.backend.AbstractIntegrationTest;
import ru.skillbox.zerone.backend.model.entity.WebSocketConnection;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WebSocketConnectionRepositoryTest extends AbstractIntegrationTest {

  @Autowired
  private WebSocketConnectionRepository repository;
  private final UUID firstUserSessionId = UUID.randomUUID();
  private final UUID secondUserSessionId = UUID.randomUUID();
  private final String firstUserId = "100";
  private final String secondUserId = "200";
  private final WebSocketConnection firstUserConnectionOne = new WebSocketConnection(firstUserSessionId, firstUserId);
  private final WebSocketConnection firstUserConnectionTwo = new WebSocketConnection(UUID.randomUUID(), firstUserId);
  private final WebSocketConnection secondUserConnection = new WebSocketConnection(secondUserSessionId, secondUserId);


  @BeforeEach
  void setUp() {
      repository.saveAll(List.of(firstUserConnectionOne, firstUserConnectionTwo, secondUserConnection));
  }

  @Test
  void testFindAllByUserId_whenTwoSessionForOneUserExist_thenReturnCorrectResult() {
    assertEquals(List.of(firstUserConnectionOne, firstUserConnectionTwo).size(), repository.findAllByUserId(firstUserId).size());
  }

  @Test
  void testFindAllByUserId_whenWrongUserId_thenReturnEmptyList() {
    assertEquals(Collections.emptyList(), repository.findAllByUserId("wrongId"));
  }

  @Test
  void testExistsByUserId_whenValidUserId_thenReturnTrue() {
    assertTrue(repository.existsByUserId(firstUserId));
  }

  @Test
  void testExistsByUserId_whenWrongUserId_thenReturnFalse() {
    assertFalse(repository.existsByUserId("wrongId"));
  }
}