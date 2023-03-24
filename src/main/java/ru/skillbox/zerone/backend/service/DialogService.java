package ru.skillbox.zerone.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.skillbox.zerone.backend.model.enumerated.ReadStatus;
import ru.skillbox.zerone.backend.repository.DialogRepository;
import ru.skillbox.zerone.backend.repository.MessageRepository;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;
import ru.skillbox.zerone.backend.util.ResponseUtils;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class DialogService {

  private final DialogRepository dialogRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final MessageMapper messageMapper;
  private final DialogMapper dialogMapper;
  private final SocketIOService socketIOService;

  @Transactional
  @SuppressWarnings("SimplifyStreamApiCallChains")
  public CommonListResponseDTO<MessageDataDTO> getMessages(long id, int offset, int itemPerPage) {
    var dialog = dialogRepository.findById(id)
        .orElseThrow(() -> new DialogException(String.format("Диалог с id: \"%s\" не найден", id)));

    var pageRequest = PageRequest.of(offset / itemPerPage, itemPerPage).withSort(Sort.by("id").descending());

    var messagesPage = messageRepository.findByDialog(dialog, pageRequest);

    var unreadedMessages = messagesPage.getContent().stream()
        .filter(m -> ReadStatus.SENT.equals(m.getReadStatus()))
        .map(u -> {
          u.setReadStatus(ReadStatus.READ);
          return u;
        })
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
    var message = messageMapper.messageRequestDTOToMessage(messageRequestDTO, dialog);
    messageRepository.save(message);

    socketIOService.sendMessageEvent(message);

    var responseData = messageMapper.messageToMessageDataDTO(message);

    return ResponseUtils.commonResponseWithData(responseData);
  }

  public CommonResponseDTO<CountDTO> getUnreaded() {
    var user = CurrentUserUtils.getCurrentUser();
    var countUnread = dialogRepository.countUnreadMessagesByUser(user, ReadStatus.SENT);

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
    var optionalDialog = dialogRepository.findByUserDuet(user, companion);

    if (optionalDialog.isEmpty()) {
      var dialog = Dialog.builder()
          .sender(user)
          .recipient(companion)
          .build();
      dialogRepository.save(dialog);

      var message = Message.builder()
          .messageText(String.format("%s начал(а) беседу", user.getFirstName()))
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
        .orElseThrow(() -> new DialogException(String.format("Для диалога с id %s не найдено сообщений", dialog.getId())));
    int unreadCount = messageRepository.countByDialogAndAuthorAndReadStatus(dialog, companion, ReadStatus.SENT);
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
              .orElseThrow(() -> new DialogException(String.format("Для диалога с id %s не найдено сообщений", d.getId())));
          int unreadCount = messageRepository.countByDialogAndAuthorAndReadStatus(d, companion, ReadStatus.SENT);
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