package ru.skillbox.zerone.backend.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skillbox.zerone.backend.model.dto.response.MessageDataDTO;
import ru.skillbox.zerone.backend.model.entity.Message;


@Mapper
public interface MessageMapper {
  @Mapping(target = "time", source = "message.sentTime")
  @Mapping(target = "sendByMe", expression = "java(ru.skillbox.zerone.backend.util.CurrentUserUtils.getCurrentUser().equals(message.getAuthor()))")
  @Mapping(target = "authorId", source = "message.author.id")
  MessageDataDTO messageToMessageDataDTO (Message message);
}
