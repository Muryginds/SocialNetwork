package ru.skillbox.zerone.backend.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skillbox.zerone.backend.model.dto.response.DialogDataDTO;
import ru.skillbox.zerone.backend.model.entity.Dialog;
import ru.skillbox.zerone.backend.model.entity.Message;
import ru.skillbox.zerone.backend.model.entity.User;

@Mapper(uses = {UserMapper.class, MessageMapper.class})
public interface DialogMapper {
  @Mapping(target = "id", source = "dialog.id")
  DialogDataDTO dialogToDialogDataDTO(Dialog dialog, Message lastMessage, int unreadCount, User companion);
}
