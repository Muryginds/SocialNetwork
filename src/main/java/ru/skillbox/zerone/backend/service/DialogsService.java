package ru.skillbox.zerone.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.DialogException;
import ru.skillbox.zerone.backend.exception.UserNotFoundException;
import ru.skillbox.zerone.backend.mapstruct.MessageMapper;
import ru.skillbox.zerone.backend.model.dto.request.DialogRequestDTO;
import ru.skillbox.zerone.backend.model.dto.request.MessageRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.*;
import ru.skillbox.zerone.backend.model.entity.Dialog;
import ru.skillbox.zerone.backend.model.enumerated.ReadStatus;
import ru.skillbox.zerone.backend.repository.DialogRepository;
import ru.skillbox.zerone.backend.repository.MessageRepository;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;
import ru.skillbox.zerone.backend.util.ResponseUtils;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DialogsService {

  private final DialogRepository dialogRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final MessageMapper messageMapper;

  public CommonListResponseDTO<MessageDataDTO> getMessages(Long id, String query, int offset, int itemPerPage, int fromMessageId) {
    return null;
  }

  public CommonResponseDTO<MessageDataDTO> postMessages(Long id, MessageRequestDTO messageRequestDTO) {
    return null;
  }

  public CommonResponseDTO<CountDTO> getUnreaded() {
    var user = CurrentUserUtils.getCurrentUser();
    var countUnread = dialogRepository.findAllByUser(user).stream()
        .mapToInt(d -> {
          var companion = user.equals(d.getRecipient()) ? d.getSender() : d.getRecipient();
          return messageRepository.countByDialogAndAuthorAndReadStatus(d, companion, ReadStatus.SENT);
        }).sum();
    return ResponseUtils.commonResponseWithData(new CountDTO(countUnread));
  }

  public CommonResponseDTO<DialogDataDTO> postDialogs(DialogRequestDTO dialogRequestDTO) {
    var id = dialogRequestDTO.getUserIds().get(0);
    if (Objects.isNull(id) || id < 0) {
      throw new DialogException(String.format("Wrong id: \"%s\" given", id));
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

      var dialogDTO = DialogDataDTO.builder()
          .recipientId(dialog.getRecipient().getId())
          .id(dialog.getId())
          .unreadCount(0)
          .build();

      return ResponseUtils.commonResponseWithData(dialogDTO);
    }

    var dialog = optionalDialog.get();
    var lastMessage = messageRepository.findFirstByDialogAndAuthorOrderBySentTimeDesc(dialog, companion).orElse(null);
    //lastMessage - любое или unread? если оно не unread - надо менять на статус read?

    var dialogDTO = DialogDataDTO.builder()
        .id(dialog.getId())
        .recipientId(companion.getId())
        .unreadCount(messageRepository.countByDialogAndAuthorAndReadStatus(dialog, companion, ReadStatus.SENT))
        .lastMessage(messageMapper.messageToMessageDataDTO(lastMessage))
        .build();

    return ResponseUtils.commonResponseWithData(dialogDTO);
  }

  public CommonListResponseDTO<DialogDataDTO> getDialogs(String name, int offset, int itemPerPage) {
    var user = CurrentUserUtils.getCurrentUser();
    var pageRequest = PageRequest.of(offset / itemPerPage, itemPerPage);

    Page<Dialog> dialogs;
    if (name.isBlank()) {
      dialogs = dialogRepository.getPageOfDialogsByUser(user, pageRequest);
    } else {
      dialogs = dialogRepository.getPageOfDialogsByUserAndQuery(user, name, pageRequest);
    }

    var dialogsDTOs = dialogs.map(d -> {
          //пользователь может быть как recipient, так и sender - поэтому определяем где собеседник
          //тут на каждый диалог приходится делать 2 запроса
          var companion = user.equals(d.getRecipient()) ? d.getSender() : d.getRecipient();
          var lastMessage = messageRepository.findFirstByDialogAndAuthorOrderBySentTimeDesc(d, companion).orElse(null);
          return DialogDataDTO.builder()
              .id(d.getId())
              .recipientId(d.getRecipient().getId()) //может быть и наш пользователь и компанион, но считаем анрид всегда относительно нашего пользователя?
              .unreadCount(messageRepository.countByDialogAndAuthorAndReadStatus(d, companion, ReadStatus.SENT))
              .lastMessage(messageMapper.messageToMessageDataDTO(lastMessage))
              .build();
        })
        .toList();

    return CommonListResponseDTO.<DialogDataDTO>builder()
        .total(dialogs.getTotalPages())
        .perPage(itemPerPage)
        .offset(offset)
        .data(dialogsDTOs)
        .build();
  }
}
