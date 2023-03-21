package ru.skillbox.zerone.backend.handler;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.skillbox.zerone.backend.model.dto.socket.request.AuthRequestDTO;
import ru.skillbox.zerone.backend.model.dto.socket.request.ReadMessagesDataDTO;
import ru.skillbox.zerone.backend.model.dto.socket.request.TypingDataDTO;
import ru.skillbox.zerone.backend.service.SocketIOService;

@Component
@RequiredArgsConstructor
public class SocketIOHandler {
  private final SocketIOService socketIOService;

  @SuppressWarnings("unused")
  @OnConnect
  public void onConnect(SocketIOClient client) {
    socketIOService.handleConnection(client);
  }

  @OnEvent("auth")
  public void onAuth(SocketIOClient client, AuthRequestDTO authRequestDTO) {
    socketIOService.authRequest(client, authRequestDTO);
  }

  @OnEvent("start-typing")
  public void onStartTyping(TypingDataDTO data) {
    socketIOService.startTyping(data);
  }

  @OnEvent("stop-typing")
  public void onStopTyping(TypingDataDTO data) {
    socketIOService.stopTyping(data);
  }

  @OnEvent("read-messages")
  public void onReadMessages(SocketIOClient client, ReadMessagesDataDTO data) {
    socketIOService.readMessages(client, data);
  }

  @OnEvent("newListener")
  public void newListener(SocketIOClient client) {
    socketIOService.newListener(client);
  }

  @SuppressWarnings("unused")
  @OnDisconnect
  public void disconnect(SocketIOClient client) {
    socketIOService.disconnect(client);
  }
}
