package ru.skillbox.zerone.backend;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.random.RandomGenerator;

@SuppressWarnings("resource")
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
abstract public class AbstractIntegrationTest {
  public static final RandomGenerator random = RandomGenerator.getDefault();

  static {
    var redis = new GenericContainer<>(DockerImageName.parse("redis:6.2.11-alpine"))
        .withExposedPorts(6379);
    redis.start();
    System.setProperty("spring.data.redis.host", redis.getHost());
    System.setProperty("spring.data.redis.port", redis.getMappedPort(6379).toString());
  }

  @MockBean
  JavaMailSenderImpl javaMailSender;
}
