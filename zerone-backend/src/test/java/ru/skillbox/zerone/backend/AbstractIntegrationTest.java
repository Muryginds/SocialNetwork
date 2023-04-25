package ru.skillbox.zerone.backend;

import com.google.api.services.drive.Drive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import ru.skillbox.zerone.backend.configuration.GoogleDriveConfig;

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

  @MockBean
  private GoogleDriveConfig googleDriveConfig;
  @MockBean
  private Drive drive;
}
