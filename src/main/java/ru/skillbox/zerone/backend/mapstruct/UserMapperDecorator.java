package ru.skillbox.zerone.backend.mapstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skillbox.zerone.backend.model.dto.request.RegisterRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;
import ru.skillbox.zerone.backend.model.entity.Role;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.repository.WebSocketConnectionRepository;
import ru.skillbox.zerone.backend.service.RoleService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class UserMapperDecorator implements UserMapper {
  @Autowired
  private UserMapper userMapper;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private RoleService roleService;
  @Autowired
  private WebSocketConnectionRepository webSocketConnectionRepository;

  @Override
  public User registerRequestDTOToUser(RegisterRequestDTO registerRequestDTO) {
    var user = userMapper.registerRequestDTOToUser(registerRequestDTO);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    List<Role> roles = new ArrayList<>();
    roles.add(roleService.getBasicUserRole());
    user.setRoles(roles);
    return user;
  }

  @Override
  public UserDTO userToUserDTO(User user) {
    var userDTO = userMapper.userToUserDTO(user);
    if (webSocketConnectionRepository.existsById(user.getId())) {
      userDTO.setLastOnlineTime(LocalDateTime.now());
    }
    return userDTO;
  }
}
