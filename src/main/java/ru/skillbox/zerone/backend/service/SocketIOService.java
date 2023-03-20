package ru.skillbox.zerone.backend.service;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.DialogException;
import ru.skillbox.zerone.backend.exception.UserNotFoundException;
import ru.skillbox.zerone.backend.model.dto.socket.request.AuthRequestDTO;
import ru.skillbox.zerone.backend.model.dto.socket.request.ReadMessagesDataDTO;
import ru.skillbox.zerone.backend.model.dto.socket.request.TypingDataDTO;
import ru.skillbox.zerone.backend.model.dto.socket.response.StartTypingResponseDTO;
import ru.skillbox.zerone.backend.model.enumerated.ReadStatus;
import ru.skillbox.zerone.backend.repository.DialogRepository;
import ru.skillbox.zerone.backend.repository.MessageRepository;
import ru.skillbox.zerone.backend.repository.SocketIORepository;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.security.JwtTokenProvider;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocketIOService {
  public static final String DIALOG_NOT_FOUND_WITH_ID_MESSAGE_PATTERN = "Диалог с id: \"%s\" не найден";
  private final SocketIOServer server;
  private final SocketIORepository socketIORepository;
  private final DialogRepository dialogRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;

  public void authRequest(SocketIOClient client, AuthRequestDTO authRequestDTO) {
    String token = authRequestDTO.getToken();
    if (token == null) {
      client.sendEvent("auth-response", "not");
      return;
    }
    String email = jwtTokenProvider.getUsername(token);
    var user = userRepository.findUserByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    var sessionId = client.getSessionId();
    socketIORepository.saveSession(user.getId(), sessionId);
    client.sendEvent("auth-response", "ok");
  }

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

  public void readMessages(SocketIOClient client, ReadMessagesDataDTO data) {
    var dialog = dialogRepository.findById(data.getDialogId())
        .orElseThrow(() -> new DialogException(String.format(DIALOG_NOT_FOUND_WITH_ID_MESSAGE_PATTERN, data.getDialogId())));
    var user = CurrentUserUtils.getCurrentUser();
    var unreadMessagesCount = messageRepository.countByDialogAndAuthorNotAndReadStatus(dialog, user, ReadStatus.SENT);
    client.sendEvent("unread-response", unreadMessagesCount);
  }

  public void handleConnection(SocketIOClient client) {
    log.debug(client.getSessionId().toString() + " connected");
  }
}
