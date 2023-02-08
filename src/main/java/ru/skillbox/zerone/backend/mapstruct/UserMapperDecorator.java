package ru.skillbox.zerone.backend.mapstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skillbox.zerone.backend.model.dto.request.RegisterRequestDTO;
import ru.skillbox.zerone.backend.model.entity.User;

public abstract class UserMapperDecorator implements UserMapper {
  @Autowired
  private UserMapper userMapper;
  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public User registerRequestDTOToUser(RegisterRequestDTO registerRequestDTO) {
    var user = userMapper.registerRequestDTOToUser(registerRequestDTO);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return user;
  }
}
