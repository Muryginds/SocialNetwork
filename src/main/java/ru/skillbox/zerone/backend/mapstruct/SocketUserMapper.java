package ru.skillbox.zerone.backend.mapstruct;

import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.dto.socket.response.SocketUserDTO;
import ru.skillbox.zerone.backend.model.entity.User;

import java.time.ZoneId;

@Service
public class SocketUserMapper {
  public SocketUserDTO userToSocketUserDTO(User user) {
    return SocketUserDTO.builder()
        .id(user.getId())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .country(user.getCountry())
        .city(user.getCity())
        .birthDate(user.getBirthDate().atStartOfDay(ZoneId.systemDefault()).toInstant())
        .regDate(user.getRegDate().atZone(ZoneId.systemDefault()).toInstant())
        .photo(user.getPhoto())
        .about(user.getAbout())
        .isBlocked(user.getIsBlocked())
        .isDeleted(user.getIsDeleted())
        .messagePermissions(user.getMessagePermissions())
        .lastOnlineTime(user.getLastOnlineTime().atZone(ZoneId.systemDefault()).toInstant())
        .phone(user.getPhone())
        .build();
  }
}
