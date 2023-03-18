package ru.skillbox.zerone.backend.controller;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.skillbox.zerone.backend.model.dto.socket.request.AuthRequest;
import ru.skillbox.zerone.backend.model.dto.socket.request.ReadMessagesData;
import ru.skillbox.zerone.backend.model.dto.socket.request.TypingData;
import ru.skillbox.zerone.backend.service.SockerIOService;

import java.rmi.UnexpectedException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocketIOController {

  private final SockerIOService sockerIOService;

  @OnConnect()
  public void onConnect(SocketIOClient client) {
    log.info(client.getSessionId().toString() + " connected");
  }

  @OnEvent("auth")
  public void onAuth(SocketIOClient client, AuthRequest authRequest) {
    sockerIOService.authRequest(client, authRequest);
  }

  @OnEvent("start-typing")
  public void onStartTyping(SocketIOClient client, TypingData data) throws UnexpectedException {
    sockerIOService.startTyping(client, data);
  }

  @OnEvent("stop-typing")
  public void onStopTyping(SocketIOClient client, TypingData data) throws UnexpectedException {
    sockerIOService.stopTyping(client, data);
  }

  @OnEvent("read-messages")
  public void onReadMessages(SocketIOClient client, ReadMessagesData data) {
    sockerIOService.readMessages(client, data);
  }

}
