package ru.skillbox.zerone.backend.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skillbox.zerone.backend.model.dto.response.DialogDataDTO;
import ru.skillbox.zerone.backend.model.entity.Dialog;
import ru.skillbox.zerone.backend.model.entity.Message;

@Mapper
public interface DialogMapper {

  @Mapping(target = "recipientId", source = "dialog.recipient.id")
  @Mapping(target = "id", source = "dialog.id")
  DialogDataDTO dialogToDialogDataDTO (Dialog dialog, Message lastMessage, int unreadCount);
}
