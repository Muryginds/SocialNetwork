package ru.skillbox.zerone.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class JpaUserDetails implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  public User loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findUserByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException(String.format("User with email: %s not found", username)));
  }
}
