package ru.skillbox.zerone.backend.configuration;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SocketIOConfig {

  @Value("${socket-server.host}")
  private String host;

  @Value("${socket-server.port}")
  private Integer port;

  @Bean
  public SocketIOServer socketIOServer() {
    Configuration config = new Configuration();
    config.setHostname(host);
    config.setPort(port);

    SocketIOServer socketIOServer = new SocketIOServer(config);
    socketIOServer.start();
    return socketIOServer;
  }

  @Bean
  public SpringAnnotationScanner scanner(SocketIOServer server) {
    return new SpringAnnotationScanner(server);
  }

}
