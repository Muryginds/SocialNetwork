package ru.skillbox.zerone.backend.security;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.security.jwt.JwtUserFactory;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    var userOptional = userRepository.findUserByEmail(email);

    if (userOptional.isEmpty()) {
      throw new UsernameNotFoundException(String.format("User with email: %s not found", email));
    }

    var jwtUser = JwtUserFactory.create(userOptional.get());
    log.info("IN loadUserByUsername - user with username: {} successfully loaded", email);

    return jwtUser;
  }
}