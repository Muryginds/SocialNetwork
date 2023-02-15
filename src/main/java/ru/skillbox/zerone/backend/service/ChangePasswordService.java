package ru.skillbox.zerone.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.dto.request.ChangePasswordTokenDto;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

@Service
@RequiredArgsConstructor
public class ChangePasswordService {
  private final UserRepository userRepository;

  private final AuthenticationManager authenticationManager;
  @Autowired
  private PasswordEncoder passwordEncoder;

  @Transactional
  public CommonResponseDTO<MessageResponseDTO> changePassword(ChangePasswordTokenDto request) {

    User user = CurrentUserUtils.getCurrentUser();
    String password = request.getPassword();


    user.setPassword(passwordEncoder.encode(password));
    userRepository.save(user);


    return CommonResponseDTO.<MessageResponseDTO>builder()
        .data(new MessageResponseDTO("ok"))
        .build();

  }
}
