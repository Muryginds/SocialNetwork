package ru.skillbox.zerone.backend.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skillbox.zerone.backend.model.dto.socket.response.SocketUserDTO;
import ru.skillbox.zerone.backend.model.entity.User;

import java.time.Instant;
import java.time.ZoneId;

@Mapper
public interface SocketUserMapper {
  @Mapping(target = "birthDate", expression = "java(birthDate)")
  @Mapping(target = "regDate", expression = "java(regDate)")
  @Mapping(target = "lastOnlineTime", expression = "java(lastOnlineTime)")
  SocketUserDTO userToSocketUserDTO(User user, Instant birthDate, Instant regDate, Instant lastOnlineTime);
}
