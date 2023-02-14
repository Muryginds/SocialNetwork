package ru.skillbox.zerone.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.RegistrationCompleteException;
import ru.skillbox.zerone.backend.exception.UserAlreadyExistException;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.UserDTO;
import ru.skillbox.zerone.backend.model.dto.request.RegisterConfirmRequestDTO;
import ru.skillbox.zerone.backend.model.dto.request.RegisterRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.model.enumerated.UserStatus;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final MailService mailService;
  private final UserMapper userMapper;

  @Transactional
  public CommonResponseDTO<MessageResponseDTO> registerAccount(RegisterRequestDTO request) {

    if (userRepository.existsByEmail(request.getEmail())) {
      throw new UserAlreadyExistException(request.getEmail());
    }

    User user = userMapper.registerRequestDTOToUser(request);
    var verificationUuid = UUID.randomUUID();
    user.setConfirmationCode(verificationUuid.toString());

    userRepository.save(user);

    mailService.sendVerificationEmail(user.getEmail(), verificationUuid.toString());

    log.info("IN registerAccount - user with username: {} successfully registered", request.getEmail());

    return CommonResponseDTO.<MessageResponseDTO>builder()
        .data(new MessageResponseDTO("ok"))
        .build();
  }

  @Transactional
  public CommonResponseDTO<MessageResponseDTO> registrationConfirm(RegisterConfirmRequestDTO request) {
    var userOptional = userRepository.findUserByEmail(request.getEmail());
    if (userOptional.isEmpty()) {
      log.info("IN registrationConfirm - user with username: {} put wrong user name", request.getEmail());
      throw new RegistrationCompleteException("wrong email or key");
    }
    User user = userOptional.get();

    if (!user.getConfirmationCode().equals(request.getConfirmationKey())) {
      log.info("IN registrationConfirm - user with username: {} put wrong confirmation key", request.getEmail());
      throw new RegistrationCompleteException("wrong email or key");
    }

    user.setIsApproved(true);
    user.setStatus(UserStatus.ACTIVE);
    userRepository.save(user);

    log.info("IN registrationConfirm - user with username: {} successfully confirmed registration", request.getEmail());

    return CommonResponseDTO.<MessageResponseDTO>builder()
        .data(new MessageResponseDTO("ok"))
        .build();
  }

  public CommonResponseDTO<UserDTO> getCurrentUser() {

    User user = CurrentUserUtils.getCurrentUser();

    log.info("IN getCurrentUser - user with username: {} successfully loaded", user.getEmail());

    return CommonResponseDTO.<UserDTO>builder()
        .data(userMapper.userToUserDTO(user))
        .build();
  }
}