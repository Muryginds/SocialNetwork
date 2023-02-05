package ru.skillbox.zerone.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.RegistrationCompleteException;
import ru.skillbox.zerone.backend.model.dto.UserDTO;
import ru.skillbox.zerone.backend.model.dto.request.RegisterConfirmRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.model.enumerated.UserStatus;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.exception.UserAlreadyExistException;
import ru.skillbox.zerone.backend.model.dto.request.RegisterRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.security.jwt.JwtUser;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final MailService mailService;

  @Transactional
  public CommonResponseDTO<MessageResponseDTO> registerAccount(RegisterRequestDTO request) {

    if (userRepository.existsByEmail(request.getEmail())) {
      throw new UserAlreadyExistException(request.getEmail());
    }

    User user = createUserFromRequest(request);
    var verificationUuid = UUID.randomUUID();
    user.setConfirmationCode(verificationUuid.toString());

    userRepository.save(user);

    mailService.sendVerificationEmail(user.getEmail(), verificationUuid.toString());

    CommonResponseDTO<MessageResponseDTO> response = new CommonResponseDTO<>();
    response.setData(new MessageResponseDTO("ok"));

    log.info("IN registerAccount - user with username: {} successfully registered", request.getEmail());
    return response;
  }

  @Transactional
  public CommonResponseDTO<MessageResponseDTO> registrationConfirm(RegisterConfirmRequestDTO request) {
    var userOptional = userRepository.findUserByEmail(request.getUserId());
    if (userOptional.isEmpty()) {
      throw new RegistrationCompleteException("wrong input");
    }
    User user = userOptional.get();

    if (!user.getConfirmationCode().equals(request.getToken())) {
      throw new RegistrationCompleteException("wrong confirmation key");
    }

    user.setIsApproved(true);
    user.setStatus(UserStatus.ACTIVE);
    userRepository.save(user);

    CommonResponseDTO<MessageResponseDTO> response = new CommonResponseDTO<>();
    response.setData(new MessageResponseDTO("ok"));

    log.info("IN registrationConfirm - user with username: {} successfully confirmed registration", request.getUserId());
    return response;
  }

  public CommonResponseDTO<UserDTO> getCurrentUser() {

    JwtUser currentUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    var userOptional = userRepository.findUserByEmail(currentUser.getEmail());

    if (userOptional.isEmpty()) {
      throw new BadCredentialsException("Invalid username or password");
    }

    CommonResponseDTO<UserDTO> responseDto = new CommonResponseDTO<>();
    responseDto.setData(UserDTO.fromUser(userOptional.get()));
    return responseDto;
  }

  private User createUserFromRequest(RegisterRequestDTO request) {
    return User.builder()
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .email(request.getEmail())
        .lastOnlineTime(LocalDateTime.now())
        .password(passwordEncoder.encode(request.getPassword()))
        .build();
  }
}