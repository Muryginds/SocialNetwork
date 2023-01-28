package ru.skillbox.zerone.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.exception.UserAlreadyExistException;
import ru.skillbox.zerone.backend.model.dto.request.RegisterRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.entity.User;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public CommonResponseDTO<Object> registerAccount(RegisterRequestDTO request) {

    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new UserAlreadyExistException(request.getEmail());
    }
    var response = CommonResponseDTO.builder()
        .data(new MessageResponseDTO("ok"))
        .build();
    User user = createUserFromRequest(request);
    userRepository.save(user);

    return response;
  }

  public User createUserFromRequest(RegisterRequestDTO request) {
    return User.builder()
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .email(request.getEmail())
        .regDate(LocalDateTime.now())
        .lastOnlineTime(LocalDateTime.now())
        .password(passwordEncoder.encode(request.getPassword()))
        .build();
  }
}