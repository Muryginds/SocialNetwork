package ru.skillbox.zerone.backend.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skillbox.zerone.backend.exception.DialogException;
import ru.skillbox.zerone.backend.mapstruct.DialogMapper;
import ru.skillbox.zerone.backend.mapstruct.MessageMapper;
import ru.skillbox.zerone.backend.model.dto.request.DialogRequestDTO;
import ru.skillbox.zerone.backend.model.dto.request.MessageRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageDataDTO;
import ru.skillbox.zerone.backend.model.entity.Dialog;
import ru.skillbox.zerone.backend.model.entity.Message;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.repository.DialogRepository;
import ru.skillbox.zerone.backend.repository.FriendshipRepository;
import ru.skillbox.zerone.backend.repository.MessageRepository;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DialogServiceUnitTest {
  @Mock
  private DialogRepository dialogRepository;
  @Mock
  private MessageRepository messageRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private MessageMapper messageMapper;
  @Mock
  private DialogMapper dialogMapper;
  @Mock
  private SocketIOService socketIOService;
  @Mock
  private FriendshipRepository friendshipRepository;
  @Mock
  private NotificationService notificationService;
  @InjectMocks
  private DialogService dialogService;

  private User currentTestUser;
  User companion;
  private final MockedStatic<CurrentUserUtils> utilsMockedStatic = Mockito.mockStatic(CurrentUserUtils.class);
  Dialog dialog;
  Message message;
  LocalDateTime MessageDate;
  MessageDataDTO messageDataDTO;
  MessageRequestDTO messageRequestDTO;

  @BeforeEach
  public void init() {
    currentTestUser = new User().setId(1L);
    companion = new User().setId(1L);
    utilsMockedStatic.when(CurrentUserUtils::getCurrentUser).thenReturn(currentTestUser);
    MessageRequestDTO messageRequestDTO = new MessageRequestDTO();
    messageRequestDTO.setMessageText("test message");
    dialog = new Dialog();
    dialog.setId(1L);
    dialog.setRecipient(new User());
    dialog.getRecipient().setId(2L);
    dialog.setSender(new User());
    dialog.getSender().setId(3L);
    message = new Message();
    message.setId(1L);
    message.setMessageText("test message");
    message.setDialog(dialog);
    MessageDate = LocalDateTime.of(2023, 1, 27, 17, 58,18,480000000);
    messageDataDTO = new MessageDataDTO(message.getMessageText(),true,"SEND",dialog.getId(),1L,MessageDate,100L);
  }

  @AfterEach
  void tearDown() {
    utilsMockedStatic.close();
  }

  @Test
  void testPostMessage() {
    when(dialogRepository.findById(any(Long.class))).thenReturn(Optional.of(dialog));
    when(messageMapper.messageRequestDTOToMessage(messageRequestDTO, dialog)).thenReturn(message);
    when(messageMapper.messageToMessageDataDTO(message)).thenReturn(messageDataDTO);
    when(friendshipRepository.findBySrcPersonAndDstPerson(any(User.class), any(User.class))).thenReturn(Optional.empty());
    CommonResponseDTO<MessageDataDTO> response = dialogService.postMessages(1L, messageRequestDTO);
    assertEquals("test message",response.getData().getMessageText());
    assertEquals("SEND",response.getData().getReadStatus());
    verify(messageRepository).save(message);
    verify(notificationService).saveMessage(message);
  }

  @Test
  void testPostDialogs_whenInvalidInput_shouldThrowException() {
    var id = -1L;
    var user = new User();
    user.setId(2L);
    user.setFirstName("Test User");

    var usersIds = new ArrayList<Long>();
    usersIds.add(id);

    var dialogRequestDTO = new DialogRequestDTO();
    dialogRequestDTO.setUsersIds(usersIds);

    when(CurrentUserUtils.getCurrentUser()).thenReturn(user);

    assertThrows(DialogException.class, () -> dialogService.postDialogs(dialogRequestDTO));
  }
}
