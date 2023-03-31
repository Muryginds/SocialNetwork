package ru.skillbox.zerone.backend;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@SuppressWarnings("resource")
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
abstract public class AbstractIntegrationTest {

  static {
    var redis = new GenericContainer<>(DockerImageName.parse("redis:6.2.11-alpine"))
        .withExposedPorts(6379);
    redis.start();
    System.setProperty("spring.data.redis.host", redis.getHost());
    System.setProperty("spring.data.redis.port", redis.getFirstMappedPort().toString());
  }

}
