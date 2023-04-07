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
import ru.skillbox.zerone.backend.exception.ZeroneSocketException;
import ru.skillbox.zerone.backend.mapstruct.MessageMapper;
import ru.skillbox.zerone.backend.model.dto.socket.request.AuthRequestDTO;
import ru.skillbox.zerone.backend.model.dto.socket.request.ReadMessagesDataDTO;
import ru.skillbox.zerone.backend.model.dto.socket.request.TypingDataDTO;
import ru.skillbox.zerone.backend.model.dto.socket.response.SocketListResponseDTO;
import ru.skillbox.zerone.backend.model.dto.socket.response.StartTypingResponseDTO;
import ru.skillbox.zerone.backend.model.entity.Dialog;
import ru.skillbox.zerone.backend.model.entity.Message;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.model.entity.WebSocketConnection;
import ru.skillbox.zerone.backend.model.enumerated.ReadStatus;
import ru.skillbox.zerone.backend.repository.*;
import ru.skillbox.zerone.backend.security.JwtTokenProvider;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static ru.skillbox.zerone.backend.model.enumerated.FriendshipStatus.BLOCKED;
import static ru.skillbox.zerone.backend.model.enumerated.ReadStatus.SENT;

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
  private final FriendshipRepository friendshipRepository;
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
    if (isNull(token)) {
      client.sendEvent(AUTH_RESPONSE_EVENT_TITLE, "not");
      return;
    }
    String email = jwtTokenProvider.getUsername(token);
    userRepository.findUserByEmail(email).ifPresentOrElse(user -> {
          WebSocketConnection connection = WebSocketConnection.builder()
              .userId(user.getId().toString())
              .sessionId(client.getSessionId())
              .build();
          webSocketConnectionRepository.save(connection);
          user.setLastOnlineTime(LocalDateTime.now());
          userRepository.save(user);

          client.sendEvent(AUTH_RESPONSE_EVENT_TITLE, "ok");
        },
        () -> client.sendEvent(AUTH_RESPONSE_EVENT_TITLE, "not"));
  }

  public void typingEvent(TypingDataDTO data, String type) {
    validateAndFindDialogById(data.getDialogId()).ifPresent(dialog -> {
      if (isSelfVaultMessage(dialog)) {
        return;
      }
      var sender = dialog.getRecipient().getId().equals(data.getAuthorId()) ? dialog.getRecipient() : dialog.getSender();
      var receiver = dialog.getRecipient().getId().equals(data.getAuthorId()) ? dialog.getSender() : dialog.getRecipient();

      if (friendshipRepository.existsBySrcPersonAndDstPersonAndStatus(receiver, sender, BLOCKED)) {
        return;
      }

      var sessionIdList = webSocketConnectionRepository.findAllByUserId(receiver.getId().toString());
      sessionIdList.forEach(s -> {
        switch (type.toLowerCase()) {
          case "start" -> startTyping(s.getSessionId(), sender, dialog.getId());
          case "stop" -> stopTyping(s.getSessionId(), sender, dialog.getId());
          default -> throw new NoSuchElementException(String.format("Неверный тип события: %s", type));
        }
      });
    });
  }

  private Optional<Dialog> validateAndFindDialogById(Long dialogId) {
    if (isNull(dialogId)) {
      return Optional.empty();
    }
    var dialog = dialogRepository.findById(dialogId)
        .orElseThrow(() -> new DialogException(String.format(DIALOG_NOT_FOUND_WITH_ID_MESSAGE_PATTERN, dialogId)));
    return Optional.of(dialog);
  }

  private boolean isSelfVaultMessage(Dialog dialog) {
    return dialog.getRecipient().getId().equals(dialog.getSender().getId());
  }

  public void startTyping(UUID sessionId, User sender, Long dialogId) {
    var curUserClient = server.getClient(sessionId);
    if (nonNull(curUserClient)) {
      var response = StartTypingResponseDTO.builder()
          .author(sender.getFirstName())
          .authorId(sender.getId())
          .dialogId(dialogId)
          .build();
      curUserClient.sendEvent("start-typing-response", response);
    }
  }

  public void stopTyping(UUID sessionId, User sender, Long dialogId) {
    var curUserClient = server.getClient(sessionId);
    if (nonNull(curUserClient)) {
      var response = StartTypingResponseDTO.builder()
          .authorId(sender.getId())
          .dialogId(dialogId)
          .build();
      curUserClient.sendEvent("stop-typing-response", response);
    }
  }

  @Transactional
  public void readMessages(SocketIOClient client, ReadMessagesDataDTO data) {
    validateAndFindDialogById(data.getDialogId()).ifPresent(dialog -> {
      if (isSelfVaultMessage(dialog)) {
        return;
      }
      var optionalCurUserId = webSocketConnectionRepository.findById(client.getSessionId());
      optionalCurUserId.ifPresent(connection -> {
        var user = userRepository.findById(Long.valueOf(connection.getUserId()))
            .orElseThrow(() -> new UserNotFoundException(connection.getUserId()));
        var unreadMessagesCount = messageRepository.countByDialogAndAuthorAndReadStatus(dialog, user, SENT);
        client.sendEvent("unread-response", unreadMessagesCount);
      });
    });
  }

  public void handleConnection(SocketIOClient client) {
    log.debug(String.format("Client with session: %s connected", client.getSessionId().toString()));
  }

  public void newListener(SocketIOClient client) {
    var sessionId = client.getSessionId();
    if (webSocketConnectionRepository.existsById(sessionId)) {
      client.sendEvent(AUTH_RESPONSE_EVENT_TITLE, "ok");
    } else {
      client.sendEvent(AUTH_RESPONSE_EVENT_TITLE, "not");
    }
  }

  public void sendMessageEvent(Message message) throws ZeroneSocketException {
    try {
      if (isSelfVaultMessage(message.getDialog())) {
        return;
      }

      var user = message.getDialog().getRecipient().getId().equals(message.getAuthor().getId()) ?
          message.getDialog().getSender() :
          message.getDialog().getRecipient();
      var sessionIdList = webSocketConnectionRepository.findAllByUserId(user.getId().toString());

      sessionIdList.forEach(s -> {
        var userClient = server.getClient(s.getSessionId());

        if (nonNull(userClient)) {
          var response = messageMapper.messageToSocketMessageDataDTO(message);
          var listResponse = SocketListResponseDTO.builder()
              .data(response)
              .build();

          message.setReadStatus(ReadStatus.READ);
          messageRepository.save(message);

          userClient.sendEvent("message", listResponse);
        }
      });
    } catch (Exception e) {
      throw new ZeroneSocketException(e);
    }
  }

  @Transactional
  public <T> void sendEventToPerson(User person, String event, T dto) {
    webSocketConnectionRepository.findAllByUserId(person.getId().toString())
        .forEach(socket -> {
          SocketIOClient client = server.getClient(socket.getSessionId());
          if (client != null) {
            client.sendEvent(event, dto);
          }
        });
  }

  public void disconnect(SocketIOClient client) {
    var optionalSession = webSocketConnectionRepository.findById(client.getSessionId());
    optionalSession.ifPresentOrElse(s -> {
          var user = userRepository.findById(Long.valueOf(s.getUserId()))
              .orElseThrow(() -> new UserNotFoundException(s.getUserId()));
          user.setLastOnlineTime(LocalDateTime.now());
          userRepository.save(user);
          webSocketConnectionRepository.delete(s);
          log.debug(String.format("Client with session: %s disconnected", s.getSessionId()));
        },
        () -> log.debug(String.format("No stored session found: %s", client.getSessionId())));
  }
}
