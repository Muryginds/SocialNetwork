package ru.skillbox.zerone.backend;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
class MigrationTest extends AbstractIntegrationTest {
  @Test
  void contextLoads()
  {
    assertTrue(true);
  }

}
