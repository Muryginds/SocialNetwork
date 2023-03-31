package ru.skillbox.zerone.backend.mapstruct;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.skillbox.zerone.backend.model.dto.response.DialogDataDTO;
import ru.skillbox.zerone.backend.model.entity.Dialog;
import ru.skillbox.zerone.backend.model.entity.Message;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

@Mapper(uses = {UserMapper.class, MessageMapper.class})
public interface DialogMapper {
  @Mapping(target = "id", source = "dialog.id")
  DialogDataDTO dialogToDialogDataDTO(Dialog dialog, Message lastMessage, int unreadCount, User companion);

  @AfterMapping
  default void checkIsPersonalMessageVault(@MappingTarget DialogDataDTO.DialogDataDTOBuilder dialogDataDTOBuilder) {
    var userDTO = dialogDataDTOBuilder.build().getCompanion();
    if (CurrentUserUtils.getCurrentUser().getId().equals(userDTO.getId())) {
      dialogDataDTOBuilder.companion(userDTO.setFirstName("Сохраненные").setLastName("сообщения"));
    }
  }
}
