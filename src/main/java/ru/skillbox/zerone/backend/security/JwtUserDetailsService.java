package ru.skillbox.zerone.backend.security;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    var userOptional = userRepository.findUserByEmail(email);

    if (userOptional.isEmpty()) {
      throw new UsernameNotFoundException(String.format("User with email: %s not found", email));
    }

    var jwtUser = userMapper.userToUserJwt(userOptional.get());
    log.info("IN loadUserByUsername - user with username: {} successfully loaded", email);

    return jwtUser;
  }
}