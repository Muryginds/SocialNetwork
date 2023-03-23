package ru.skillbox.zerone.backend.configuration;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.skillbox.zerone.backend.configuration.properties.SocketIOProperties;

@Configuration
@RequiredArgsConstructor
public class SocketIOConfig {
  private final SocketIOProperties socketIOProperties;

  @Bean
  public SocketIOServer socketIOServer() {
    com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
    config.setHostname(socketIOProperties.getHost());
    config.setPort(socketIOProperties.getPort());
    var socketConfig = config.getSocketConfig();
    socketConfig.setReuseAddress(true);
    config.setSocketConfig(socketConfig);
    return new SocketIOServer(config);
  }

  @Bean
  public SpringAnnotationScanner scanner(SocketIOServer server) {
    return new SpringAnnotationScanner(server);
  }
}
