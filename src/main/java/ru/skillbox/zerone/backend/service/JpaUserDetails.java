package ru.skillbox.zerone.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.UserNotFoundException;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class JpaUserDetails implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  public User loadUserByUsername(String username) throws UserNotFoundException {
    return userRepository.findUserByEmail(username)
        .orElseThrow(() -> new UserNotFoundException(String.format("User with email: %s not found", username)));
  }
}
