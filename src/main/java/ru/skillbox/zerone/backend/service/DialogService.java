package ru.skillbox.zerone.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.DialogException;
import ru.skillbox.zerone.backend.exception.UserNotFoundException;
import ru.skillbox.zerone.backend.exception.ZeroneSocketException;
import ru.skillbox.zerone.backend.mapstruct.DialogMapper;
import ru.skillbox.zerone.backend.mapstruct.MessageMapper;
import ru.skillbox.zerone.backend.model.dto.request.DialogRequestDTO;
import ru.skillbox.zerone.backend.model.dto.request.MessageRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.*;
import ru.skillbox.zerone.backend.model.entity.Dialog;
import ru.skillbox.zerone.backend.model.entity.Message;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.repository.DialogRepository;
import ru.skillbox.zerone.backend.repository.FriendshipRepository;
import ru.skillbox.zerone.backend.repository.MessageRepository;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;
import ru.skillbox.zerone.backend.util.ResponseUtils;

import java.util.Objects;

import static java.lang.Boolean.TRUE;
import static ru.skillbox.zerone.backend.model.enumerated.FriendshipStatus.BLOCKED;
import static ru.skillbox.zerone.backend.model.enumerated.FriendshipStatus.DEADLOCK;
import static ru.skillbox.zerone.backend.model.enumerated.ReadStatus.READ;
import static ru.skillbox.zerone.backend.model.enumerated.ReadStatus.SENT;

@Service
@RequiredArgsConstructor
public class DialogService {

  private final DialogRepository dialogRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final MessageMapper messageMapper;
  private final DialogMapper dialogMapper;
  private final SocketIOService socketIOService;
  private final FriendshipRepository friendshipRepository;

  @Transactional
  public CommonListResponseDTO<MessageDataDTO> getMessages(long id, int offset, int itemPerPage) {
    var dialog = dialogRepository.findById(id)
        .orElseThrow(() -> new DialogException(String.format("Диалог с id: \"%s\" не найден", id)));

    var pageRequest = PageRequest.of(offset / itemPerPage, itemPerPage).withSort(Sort.by("id").descending());

    var messagesPage = messageRepository.findByDialog(dialog, pageRequest);

    var unreadedMessages = messagesPage.stream()
        .filter(m -> SENT.equals(m.getReadStatus()))
        .map(u -> u.setReadStatus(READ))
        .toList();
    messageRepository.saveAll(unreadedMessages);

    return CommonListResponseDTO.<MessageDataDTO>builder()
        .offset(offset)
        .perPage(itemPerPage)
        .total(messagesPage.getTotalElements())
        .data(messageMapper.messagesListToMessageDataDTOs(messagesPage.getContent()))
        .build();
  }

  @Transactional(dontRollbackOn = ZeroneSocketException.class)
  public CommonResponseDTO<MessageDataDTO> postMessages(long id, MessageRequestDTO messageRequestDTO) {
    var dialog = dialogRepository.findById(id)
        .orElseThrow(() -> new DialogException(String.format("Диалог с id: \"%s\" не найден", id)));
    var user = CurrentUserUtils.getCurrentUser();
    var companion = user.getId().equals(dialog.getRecipient().getId()) ? dialog.getSender() : dialog.getRecipient();

    checkUserCanSendMessagesToCompanion(user, companion);

    var message = messageMapper.messageRequestDTOToMessage(messageRequestDTO, dialog);
    messageRepository.save(message);

    socketIOService.sendMessageEvent(message);

    var responseData = messageMapper.messageToMessageDataDTO(message);

    return ResponseUtils.commonResponseWithData(responseData);
  }

  private void checkUserCanSendMessagesToCompanion(User user, User companion) {
    if (TRUE.equals(companion.getIsBlocked())) {
      throw new DialogException("Вы не можете отправлять сообщения заблокированному пользователю");
    }

    if (TRUE.equals(companion.getIsDeleted())) {
      throw new DialogException("Вы не можете отправлять сообщения удаленному пользователю");
    }

    var optionalCompanionToUserFriendship = friendshipRepository.findBySrcPersonAndDstPerson(companion, user);
    optionalCompanionToUserFriendship.ifPresent(f -> {
      var status = f.getStatus();
      if (DEADLOCK.equals(status) || BLOCKED.equals(status)) {
        throw new DialogException("Пользователь вас заблокировал, вы не можете отправлять ему сообщения");
      }
    });
  }

  public CommonResponseDTO<CountDTO> getUnreaded() {
    var user = CurrentUserUtils.getCurrentUser();
    var countUnread = dialogRepository.countUnreadMessagesByUser(user, SENT);

    return ResponseUtils.commonResponseWithData(new CountDTO(countUnread));
  }

  @Transactional(dontRollbackOn = ZeroneSocketException.class)
  public CommonResponseDTO<DialogDataDTO> postDialogs(DialogRequestDTO dialogRequestDTO) {
    var id = dialogRequestDTO.getUsersIds().get(0);
    if (Objects.isNull(id) || id < 1) {
      throw new DialogException(String.format("Получен неверный id: \"%s\"", id));
    }
    var user = CurrentUserUtils.getCurrentUser();
    var companion = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

    checkUserCanSendMessagesToCompanion(user, companion);

    var optionalDialog = dialogRepository.findByUserDuet(user, companion);

    if (optionalDialog.isEmpty()) {
      var dialog = Dialog.builder()
          .sender(user)
          .recipient(companion)
          .build();
      dialogRepository.save(dialog);

      String firstMessageText;
      if (user.getId().equals(companion.getId())) {
        firstMessageText = "Это ваше персональное хранилище сообщений";
      } else {
        firstMessageText = String.format("%s начал(а) беседу", user.getFirstName());
      }

      var message = Message.builder()
          .messageText(firstMessageText)
          .dialog(dialog)
          .author(user)
          .build();
      messageRepository.save(message);

      socketIOService.sendMessageEvent(message);

      var dialogDataDTO = dialogMapper.dialogToDialogDataDTO(dialog, message, 0, companion);

      return ResponseUtils.commonResponseWithData(dialogDataDTO);
    }

    var dialog = optionalDialog.get();
    var lastMessage = messageRepository.findFirstByDialogOrderBySentTimeDesc(dialog)
        .orElseThrow(() -> new DialogException(String.format("Для диалога с id \"%s\" не найдено сообщений", dialog.getId())));
    int unreadCount = messageRepository.countByDialogAndAuthorAndReadStatus(dialog, companion, SENT);
    var dialogDataDTO = dialogMapper.dialogToDialogDataDTO(dialog, lastMessage, unreadCount, companion);

    return ResponseUtils.commonResponseWithData(dialogDataDTO);
  }

  @Transactional
  public CommonListResponseDTO<DialogDataDTO> getDialogs(int offset, int itemPerPage) {
    var user = CurrentUserUtils.getCurrentUser();
    var pageRequest = PageRequest.of(offset / itemPerPage, itemPerPage);

    var dialogsPage = dialogRepository.getPageOfDialogsByUser(user, pageRequest);

    var dialogsDTOs = dialogsPage.map(d -> {
          var companion = user.getId().equals(d.getRecipient().getId()) ? d.getSender() : d.getRecipient();
          var lastMessage = messageRepository.findFirstByDialogOrderBySentTimeDesc(d)
              .orElseThrow(() -> new DialogException(String.format("Для диалога с id \"%s\" не найдено сообщений", d.getId())));
          int unreadCount = messageRepository.countByDialogAndAuthorAndReadStatus(d, companion, SENT);
          return dialogMapper.dialogToDialogDataDTO(d, lastMessage, unreadCount, companion);
        })
        .toList();

    return CommonListResponseDTO.<DialogDataDTO>builder()
        .total(dialogsPage.getTotalPages())
        .perPage(itemPerPage)
        .offset(offset)
        .data(dialogsDTOs)
        .build();
  }
}
