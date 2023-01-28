package ru.skillbox.zerone.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.exception.UserAlreadyExistException;
import ru.skillbox.zerone.backend.model.dto.request.RegisterRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.ResponseDTO;
import ru.skillbox.zerone.backend.model.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  //private final PasswordEncoder passwordEncoder;

  public ResponseDTO<Object> registerAccount(RegisterRequestDTO request) {

    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new UserAlreadyExistException(request.getEmail());
    }
    var response = ResponseDTO.builder()
        .data(Map.of("Message", "ok"))
        .timestamp(LocalDateTime.now())
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
        .password(request.getPasswd1())
        .birthDate(LocalDate.now())
        .phone("")
        .country("")
        .city("")
        .confirmationCode("")
//        .password(passwordEncoder.encode(request.getPasswd1()))
        .build();
  }
}