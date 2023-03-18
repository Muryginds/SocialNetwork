package ru.skillbox.zerone.backend.configuration;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class SocketIORunner implements CommandLineRunner {

  private final SocketIOServer server;

  @Override
  public void run(String... args) throws Exception {
    server.start();
  }
}
