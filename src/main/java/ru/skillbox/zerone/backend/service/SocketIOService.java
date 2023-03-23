package ru.skillbox.zerone.backend.service;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.DialogException;
import ru.skillbox.zerone.backend.exception.UserNotFoundException;
import ru.skillbox.zerone.backend.mapstruct.MessageMapper;
import ru.skillbox.zerone.backend.model.dto.socket.request.AuthRequestDTO;
import ru.skillbox.zerone.backend.model.dto.socket.request.ReadMessagesDataDTO;
import ru.skillbox.zerone.backend.model.dto.socket.request.TypingDataDTO;
import ru.skillbox.zerone.backend.model.dto.socket.response.SocketListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.socket.response.StartTypingResponseDTO;
import ru.skillbox.zerone.backend.model.entity.Message;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.model.entity.WebSocketConnection;
import ru.skillbox.zerone.backend.model.enumerated.ReadStatus;
import ru.skillbox.zerone.backend.repository.*;
import ru.skillbox.zerone.backend.security.JwtTokenProvider;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocketIOService {
  private static final String DIALOG_NOT_FOUND_WITH_ID_MESSAGE_PATTERN = "Диалог с id: \"%s\" не найден";
  private static final String AUTH_RESPONSE_EVENT_TITLE = "auth-response";
  private final SocketIOServer server;
  private final WebSocketConnectionRepository webSocketConnectionRepository;
  private final DialogRepository dialogRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final MessageMapper messageMapper;

  @PostConstruct
  public void startOnCreate() {
    server.start();
  }

  @PreDestroy
  public void stopOnClose() {
    server.stop();
  }

  @Transactional
  public void authRequest(SocketIOClient client, AuthRequestDTO authRequestDTO) {
    String token = authRequestDTO.getToken();
    if (token == null) {
      client.sendEvent(AUTH_RESPONSE_EVENT_TITLE, "not");
      return;
    }
    String email = jwtTokenProvider.getUsername(token);
    var user = userRepository.findUserByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    var sessionId = client.getSessionId();
    webSocketConnectionRepository.save(new WebSocketConnection(user.getId(), sessionId));

    user.setLastOnlineTime(LocalDateTime.now());
    userRepository.save(user);

    client.sendEvent(AUTH_RESPONSE_EVENT_TITLE, "ok");
  }

  public void typingEvent(TypingDataDTO data, String type) {
    var dialog = dialogRepository.findById(data.getDialogId())
        .orElseThrow(() -> new DialogException(String.format(DIALOG_NOT_FOUND_WITH_ID_MESSAGE_PATTERN, data.getDialogId())));
    var curUser = dialog.getRecipient().getId().equals(data.getAuthorId()) ? dialog.getRecipient() : dialog.getSender();
    var companion = dialog.getRecipient().getId().equals(data.getAuthorId()) ? dialog.getSender() : dialog.getRecipient();

    var companionSessionId = webSocketConnectionRepository.findById(companion.getId());
    companionSessionId.ifPresent(s -> {
      switch (type) {
        case "start" -> startTyping(s.getSessionId(), curUser, dialog.getId());
        case "stop" -> stopTyping(s.getSessionId(), curUser, dialog.getId());
        default -> throw new NoSuchElementException(String.format("Wrong eventType: %s", type));
      }
    });
  }

  public void startTyping(UUID sessionId, User curUser, Long dialogId) {
    var companionClient = server.getClient(sessionId);
    if (!Objects.isNull(companionClient)) {
      var response = StartTypingResponseDTO.builder()
          .author(curUser.getFirstName())
          .authorId(curUser.getId())
          .dialogId(dialogId)
          .build();
      companionClient.sendEvent("start-typing-response", response);
    }
  }

  public void stopTyping(UUID sessionId, User curUser, Long dialogId) {
    var companionClient = server.getClient(sessionId);
    if (!Objects.isNull(companionClient)) {
      var response = StartTypingResponseDTO.builder()
          .authorId(curUser.getId())
          .dialogId(dialogId)
          .build();
      companionClient.sendEvent("stop-typing-response", response);
    }
  }

  @Transactional
  public void readMessages(SocketIOClient client, ReadMessagesDataDTO data) {
    var dialog = dialogRepository.findById(data.getDialogId())
        .orElseThrow(() -> new DialogException(String.format(DIALOG_NOT_FOUND_WITH_ID_MESSAGE_PATTERN, data.getDialogId())));
    var optionalCurUserId = webSocketConnectionRepository.findBySessionId(client.getSessionId());
    optionalCurUserId.ifPresent(connection -> {
      var user = userRepository.findById(connection.getUserId()).orElseThrow(() -> new UserNotFoundException(connection.getUserId()));
      var unreadMessagesCount = messageRepository.countByDialogAndAuthorAndReadStatus(dialog, user, ReadStatus.SENT);
      client.sendEvent("unread-response", unreadMessagesCount);
    });
  }

  public void handleConnection(SocketIOClient client) {
    log.debug(String.format("Client with session: %s connected", client.getSessionId().toString()));
  }

  public void newListener(SocketIOClient client) {
    var sessionId = client.getSessionId();
    if (webSocketConnectionRepository.existsBySessionId(sessionId)) {
      client.sendEvent(AUTH_RESPONSE_EVENT_TITLE, "ok");
    } else {
      client.sendEvent(AUTH_RESPONSE_EVENT_TITLE, "not");
    }
  }

  public void sendMessageEvent(Message message) {
    var user = message.getDialog().getRecipient().getId().equals(message.getAuthor().getId()) ?
        message.getDialog().getSender() :
        message.getDialog().getRecipient();
    var companionSessionId = webSocketConnectionRepository.findById(user.getId());

    companionSessionId.ifPresent(s -> {
      var companionClient = server.getClient(s.getSessionId());

      if (!Objects.isNull(companionClient)) {
        var response = messageMapper.messageToSocketMessageDataDTO(message);
        var listResponse = SocketListResponseDTO.builder()
            .data(response)
            .build();

        message.setReadStatus(ReadStatus.READ);
        messageRepository.save(message);

        companionClient.sendEvent("message", listResponse);
      }
    });
  }

  public void disconnect(SocketIOClient client) {
    var optionalSession = webSocketConnectionRepository.findBySessionId(client.getSessionId());
    optionalSession.ifPresent(s -> {
      var user = userRepository.findById(s.getUserId()).orElseThrow(() -> new UserNotFoundException(s.getUserId()));
      user.setLastOnlineTime(LocalDateTime.now());
      userRepository.save(user);
      webSocketConnectionRepository.delete(s);
    });
  }
}
