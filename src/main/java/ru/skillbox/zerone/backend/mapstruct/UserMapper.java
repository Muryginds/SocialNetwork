package ru.skillbox.zerone.backend.mapstruct;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import ru.skillbox.zerone.backend.model.dto.UserDTO;
import ru.skillbox.zerone.backend.model.dto.request.RegisterRequestDTO;
import ru.skillbox.zerone.backend.model.entity.User;

@Mapper
@DecoratedWith(UserMapperDecorator.class)
public interface UserMapper {
  UserDTO userToUserDTO (User user);
  User registerRequestDTOToUser(RegisterRequestDTO registerRequestDTO);
  UserDTO userToDtoWithToken(User user, String token);
}
