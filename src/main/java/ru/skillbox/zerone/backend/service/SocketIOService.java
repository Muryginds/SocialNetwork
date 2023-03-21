package ru.skillbox.zerone.backend.service;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
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
import ru.skillbox.zerone.backend.model.enumerated.ReadStatus;
import ru.skillbox.zerone.backend.repository.DialogRepository;
import ru.skillbox.zerone.backend.repository.MessageRepository;
import ru.skillbox.zerone.backend.repository.SocketIORepository;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.security.JwtTokenProvider;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocketIOService {
  private static final String DIALOG_NOT_FOUND_WITH_ID_MESSAGE_PATTERN = "Диалог с id: \"%s\" не найден";
  private static final String AUTH_RESPONSE_EVENT_TITLE = "auth-response";
  private final SocketIOServer server;
  private final SocketIORepository socketIORepository;
  private final DialogRepository dialogRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final MessageMapper  messageMapper;

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
    socketIORepository.saveSession(user.getId(), sessionId);
    client.sendEvent(AUTH_RESPONSE_EVENT_TITLE, "ok");
  }

  @Transactional
  public void startTyping(TypingDataDTO data) {
    var dialog = dialogRepository.findById(data.getDialogId())
        .orElseThrow(() -> new DialogException(String.format(DIALOG_NOT_FOUND_WITH_ID_MESSAGE_PATTERN, data.getDialogId())));
    var companion = dialog.getRecipient().getId().equals(data.getAuthorId()) ? dialog.getRecipient() : dialog.getSender();
    var companionSessionId = socketIORepository.findSessionByUserId(companion.getId());
    companionSessionId.ifPresent(s -> {
      var companionClient = server.getClient(s);
      if (!Objects.isNull(companionClient)) {
        var response = StartTypingResponseDTO.builder()
            .author(companion.getFirstName())
            .authorId(data.getAuthorId())
            .dialogId(data.getDialogId())
            .build();
        companionClient.sendEvent("start-typing-response", response);
      }
    });
  }

  @Transactional
  public void stopTyping(TypingDataDTO data) {
    var dialog = dialogRepository.findById(data.getDialogId())
        .orElseThrow(() -> new DialogException(String.format(DIALOG_NOT_FOUND_WITH_ID_MESSAGE_PATTERN, data.getDialogId())));
    var companion = dialog.getRecipient().getId().equals(data.getAuthorId()) ? dialog.getRecipient() : dialog.getSender();
    var companionSessionId = socketIORepository.findSessionByUserId(companion.getId());
    companionSessionId.ifPresent(s -> {
      var companionClient = server.getClient(s);
      if (!Objects.isNull(companionClient)) {
        var response = StartTypingResponseDTO.builder()
            .authorId(data.getAuthorId())
            .dialogId(data.getDialogId())
            .build();
        companionClient.sendEvent("stop-typing-response", response);
      }
    });
  }

  @Transactional
  public void readMessages(SocketIOClient client, ReadMessagesDataDTO data) {
    var dialog = dialogRepository.findById(data.getDialogId())
        .orElseThrow(() -> new DialogException(String.format(DIALOG_NOT_FOUND_WITH_ID_MESSAGE_PATTERN, data.getDialogId())));
    var user = CurrentUserUtils.getCurrentUser();
    var unreadMessagesCount = messageRepository.countByDialogAndAuthorNotAndReadStatus(dialog, user, ReadStatus.SENT);
    client.sendEvent("unread-response", unreadMessagesCount);
  }

  public void handleConnection(SocketIOClient client) {
    log.debug(String.format("Client with session: %s connected", client.getSessionId().toString()));
  }

  public void newListener(SocketIOClient client) {
    var sessionId = client.getSessionId();
    if (socketIORepository.checkSessionIsActive(sessionId)) {
      client.sendEvent(AUTH_RESPONSE_EVENT_TITLE, "ok");
    } else {
      client.sendEvent(AUTH_RESPONSE_EVENT_TITLE, "not");
    }
  }

  @Transactional
  public void sendMessageEvent(Message message) {
    var user = CurrentUserUtils.getCurrentUser();
    if (message.getAuthor().equals(user)) {
      return;
    }
    var companionSessionId = socketIORepository.findSessionByUserId(message.getAuthor().getId());
    companionSessionId.ifPresent(s -> {
      var companionClient = server.getClient(s);
      if (!Objects.isNull(companionClient)) {
        var response = messageMapper.messageToSocketMessageDataDTO(message);
        var listResponse = SocketListResponseDTO.builder()
            .readStatus(ReadStatus.SENT.toString())
            .data(response)
            .timestamp(Instant.now())
            .build();

        message.setReadStatus(ReadStatus.READ);
        messageRepository.save(message);

        companionClient.sendEvent("message", listResponse);
      }
    });
  }

  @Transactional
  public void disconnect(SocketIOClient client) {
    socketIORepository.deleteByUUID(client.getSessionId());
    var user = CurrentUserUtils.getCurrentUser();
    user.setLastOnlineTime(LocalDateTime.now());
    userRepository.save(user);
  }
}
