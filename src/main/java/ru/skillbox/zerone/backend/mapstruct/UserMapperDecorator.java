package ru.skillbox.zerone.backend.mapstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skillbox.zerone.backend.model.dto.request.RegisterRequestDTO;
import ru.skillbox.zerone.backend.model.entity.Role;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.service.RoleService;

import java.util.ArrayList;

public abstract class UserMapperDecorator implements UserMapper {
  @Autowired
  private UserMapper userMapper;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private RoleService roleService;

  @Override
  public User registerRequestDTOToUser(RegisterRequestDTO registerRequestDTO) {
    var user = userMapper.registerRequestDTOToUser(registerRequestDTO);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    var roles = new ArrayList<Role>();
    roles.add(roleService.getBasicUserRole());
    user.setRoles(roles);
    return user;
  }
}
