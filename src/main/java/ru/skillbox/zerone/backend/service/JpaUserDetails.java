package ru.skillbox.zerone.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class JpaUserDetails implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public User loadUserByUsername(String username) throws UsernameNotFoundException {
    var userOptional = userRepository.findUserByEmail(username);

    if (userOptional.isEmpty()) {
      throw new UsernameNotFoundException(String.format("User with email: %s not found", username));
    }

    log.info("IN loadUserByUsername - user with email: {} successfully loaded", username);

    return userOptional.get();
  }
}
