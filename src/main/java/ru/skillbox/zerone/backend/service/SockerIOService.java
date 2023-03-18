package ru.skillbox.zerone.backend.service;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.dto.socket.request.AuthRequest;
import ru.skillbox.zerone.backend.model.dto.socket.request.ReadMessagesData;
import ru.skillbox.zerone.backend.model.dto.socket.request.TypingData;
import ru.skillbox.zerone.backend.model.dto.socket.response.StartTypingResponse;
import ru.skillbox.zerone.backend.model.entity.Dialog;
import ru.skillbox.zerone.backend.repository.DialogRepository;
import ru.skillbox.zerone.backend.repository.SocketIORepository;
import ru.skillbox.zerone.backend.security.JwtTokenProvider;

import java.rmi.UnexpectedException;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class SockerIOService {

  private final SocketIOServer server;
  private final SocketIORepository socketIORepository;
  private final DialogRepository dialogRepository;
  private final JwtTokenProvider jwtTokenProvider;

  public void authRequest(SocketIOClient client, AuthRequest authRequest) {
    if (authRequest.getToken() == null) {
      client.sendEvent("auth-response", "not");
    }
    String email = jwtTokenProvider.getUsername(authRequest.getToken());
    socketIORepository.saveSessionIdEmail(client.getSessionId().toString(), email);
    client.sendEvent("auth-response", "ok");
  }

  public void startTyping(SocketIOClient client, TypingData data) throws UnexpectedException {
    startStopTyping(client, data, true);
  }

  public void stopTyping(SocketIOClient client, TypingData data) throws UnexpectedException {
    startStopTyping(client, data, false);
  }

  private void startStopTyping(SocketIOClient client, TypingData data, boolean toStart) throws UnexpectedException {
    Collection<SocketIOClient> clients = server.getAllClients();
    Dialog dialog = dialogRepository.findById((long) data.getDialog())
        .orElseThrow(() -> new UnexpectedException("Wrong dialog id"));
    clients.forEach(cl -> {
      var response = StartTypingResponse.builder()
          .authorId(data.getAuthor())
          .dialog(dialog.getId().intValue())
          .dialog(data.getDialog())
          .build();
      if (toStart) {
        cl.sendEvent("start-typing-response", response);
      } else {
        cl.sendEvent("stop-typing-response", response);
      }
    });
  }

  public void readMessages(SocketIOClient client, ReadMessagesData data) {
    int unreadMessagesCount = 0;
    client.sendEvent("unread-response", String.valueOf(unreadMessagesCount));
  }
}
