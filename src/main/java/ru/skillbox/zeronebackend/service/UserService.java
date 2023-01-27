package ru.skillbox.zeronebackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.zeronebackend.model.dto.request.RegisterRequestDTO;
import ru.skillbox.zeronebackend.model.dto.response.ResponseDTO;
import ru.skillbox.zeronebackend.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

  public ResponseDTO<Object> registerAccount(RegisterRequestDTO request) {
      return null;
  }
}