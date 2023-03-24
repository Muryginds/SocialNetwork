package ru.skillbox.zerone.backend.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skillbox.zerone.backend.model.dto.request.MessageRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageDataDTO;
import ru.skillbox.zerone.backend.model.dto.socket.response.SocketMessageDataDTO;
import ru.skillbox.zerone.backend.model.entity.Dialog;
import ru.skillbox.zerone.backend.model.entity.Message;

import java.util.List;

@Mapper
public interface MessageMapper {
  @Mapping(target = "dialogId", source = "message.dialog.id")
  @Mapping(target = "time", source = "message.sentTime")
  @Mapping(target = "sendByMe", expression = "java(ru.skillbox.zerone.backend.util.CurrentUserUtils.getCurrentUser().getId().equals(message.getAuthor().getId()))")
  @Mapping(target = "authorId", source = "message.author.id")
  MessageDataDTO messageToMessageDataDTO (Message message);

  List<MessageDataDTO> messagesListToMessageDataDTOs (List<Message> messages);

  @Mapping(target = "sentTime", ignore = true)
  @Mapping(target = "readStatus", ignore = true)
  @Mapping(target = "author", expression = "java(ru.skillbox.zerone.backend.util.CurrentUserUtils.getCurrentUser())")
  @Mapping(target = "dialog", source = "dialog")
  @Mapping(target = "id", expression = "java(null)")
  Message messageRequestDTOToMessage(MessageRequestDTO messageRequestDTO, Dialog dialog);

  @Mapping(target = "time", expression = "java(message.getSentTime().atZone(java.time.ZoneId.systemDefault()).toInstant())")
  @Mapping(target = "isSendByMe", expression = "java(ru.skillbox.zerone.backend.util.CurrentUserUtils.getCurrentUser().getId().equals(message.getAuthor().getId()))")
  @Mapping(target = "dialogId", source = "message.dialog.id")
  @Mapping(target = "authorId", source = "message.author.id")
  @Mapping(target = "id", source = "message.id")
  SocketMessageDataDTO messageToSocketMessageDataDTO(Message message);
}
