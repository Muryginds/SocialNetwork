package ru.skillbox.zerone.backend.mapstruct;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.skillbox.zerone.backend.model.dto.UserDTO;
import ru.skillbox.zerone.backend.model.dto.request.RegisterRequestDTO;
import ru.skillbox.zerone.backend.model.entity.Role;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.security.jwt.JwtUser;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
@DecoratedWith(UserMapperDecorator.class)
public interface UserMapper {

  UserDTO userToUserDTO (User user);
  @Mapping(target = "authorities", source = "roles", qualifiedByName = "setAuthorities")
  JwtUser userToUserJwt (User user);
  User registerRequestDTOToUser(RegisterRequestDTO registerRequestDTO);

  @Named("setAuthorities")
  default List<GrantedAuthority> setAuthorities(List<Role> userRoles) {
    return userRoles.stream()
        .map(role -> new SimpleGrantedAuthority(role.getName()))
        .collect(Collectors.toList());
  }
}
