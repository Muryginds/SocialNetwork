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

    CommonResponseDTO<MessageResponseDTO> response = new CommonResponseDTO<>();
    response.setData(new MessageResponseDTO("ok"));

    log.info("IN registerAccount - user with username: {} successfully registered", request.getEmail());
    return response;
  }

  @Transactional
  public CommonResponseDTO<MessageResponseDTO> registrationConfirm(RegisterConfirmRequestDTO request) {
    var userOptional = userRepository.findUserByEmail(request.getUserId());
    if (userOptional.isEmpty()) {
      log.info("IN registrationConfirm - user with username: {} put wrong user name", request.getUserId());
      throw new RegistrationCompleteException("wrong email or key");
    }
    User user = userOptional.get();

    if (!user.getConfirmationCode().equals(request.getToken())) {
      log.info("IN registrationConfirm - user with username: {} put wrong confirmation key", request.getUserId());
      throw new RegistrationCompleteException("wrong email or key");
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

    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    CommonResponseDTO<UserDTO> response = new CommonResponseDTO<>();
    response.setData(userMapper.userToUserDTO(user));

    log.info("IN getCurrentUser - user with username: {} successfully loaded", user.getEmail());
    return response;
  }
}