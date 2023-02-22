package ru.skillbox.zerone.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.ChangeEmailException;
import ru.skillbox.zerone.backend.exception.RegistrationCompleteException;
import ru.skillbox.zerone.backend.exception.UserAlreadyExistException;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;
import ru.skillbox.zerone.backend.model.dto.request.ChangeEmailDTO;
import ru.skillbox.zerone.backend.model.dto.request.ChangePasswordDTO;
import ru.skillbox.zerone.backend.model.dto.request.RegisterConfirmRequestDTO;
import ru.skillbox.zerone.backend.model.dto.request.RegisterRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.model.entity.ChangeEmailHistory;
import ru.skillbox.zerone.backend.model.enumerated.UserStatus;
import ru.skillbox.zerone.backend.repository.ChangeEmailHistoryRepository;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final ChangeEmailHistoryRepository changeEmailHistoryRepository;
  private final MailService mailService;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  public CommonResponseDTO<MessageResponseDTO> changePassword(ChangePasswordDTO request) {
    var user = CurrentUserUtils.getCurrentUser();
    var password = request.getPassword();

    user.setPassword(passwordEncoder.encode(password));
    userRepository.save(user);

    return CommonResponseDTO.<MessageResponseDTO>builder()
        .data(new MessageResponseDTO("ok"))
        .build();
  }

  @Transactional
  public CommonResponseDTO<MessageResponseDTO> registerEmailChange(ChangeEmailDTO request) {
    var user = CurrentUserUtils.getCurrentUser();
    var emailNew = request.getEmail();

    if (userRepository.existsByEmail(emailNew)) {
      throw new ChangeEmailException(String.format("User with email %s already exist", emailNew));
    }

    var emailOld = user.getEmail();

    var changeEmailHistory = ChangeEmailHistory.builder()
        .emailOld(emailOld)
        .emailNew(emailNew)
        .build();
    changeEmailHistoryRepository.save(changeEmailHistory);

    var verificationUuid = UUID.randomUUID();
    user.setConfirmationCode(verificationUuid.toString());
    userRepository.save(user);

    mailService.sendVerificationChangeEmail(emailOld, user.getConfirmationCode());

    return CommonResponseDTO.<MessageResponseDTO>builder()
        .data(new MessageResponseDTO("ok"))
        .build();
  }

  @Transactional
  public CommonResponseDTO<MessageResponseDTO> changeEmailConfirm(String emailOld, String confirmationCode) {
    var user = userRepository.findUserByEmail(emailOld)
        .orElseThrow(() -> new ChangeEmailException(String.format("Could not find user with email: %s", emailOld)));

    if (!user.getConfirmationCode().equals(confirmationCode)) {
      throw new ChangeEmailException("Wrong confirmation code");
    }

    var changeEmailHistory = changeEmailHistoryRepository.findFirstByEmailOldOrderByTimeDesc(emailOld)
        .orElseThrow(() -> new ChangeEmailException(String.format("Could not find user with email: %s", emailOld)));

    user.setEmail(changeEmailHistory.getEmailNew());
    userRepository.save(user);

    //TODO: Вместо респонда логичнее возвращать редирект на главную страницу сайта,
    // поскольку фронт при смене пароля нас разлогинивает, а если перейти по ссылке из письма,
    // в браузере увидим только ответ в формате json
    return CommonResponseDTO.<MessageResponseDTO>builder()
        .data(new MessageResponseDTO("ok"))
        .build();
  }

  @Transactional
  public CommonResponseDTO<MessageResponseDTO> registerAccount(RegisterRequestDTO request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new UserAlreadyExistException(request.getEmail());
    }

    var user = userMapper.registerRequestDTOToUser(request);
    var verificationUuid = UUID.randomUUID();
    user.setConfirmationCode(verificationUuid.toString());

    userRepository.save(user);

    mailService.sendVerificationEmail(user.getEmail(), verificationUuid.toString());

    return CommonResponseDTO.<MessageResponseDTO>builder()
        .data(new MessageResponseDTO("ok"))
        .build();
  }

  @Transactional
  public CommonResponseDTO<MessageResponseDTO> registrationConfirm(RegisterConfirmRequestDTO request) {
    var user = userRepository.findUserByEmail(request.getEmail())
        .orElseThrow(()-> new RegistrationCompleteException("Wrong email or key"));

    if (user.getIsApproved()) {
      throw new RegistrationCompleteException("User already confirmed");
    }

    if (!user.getConfirmationCode().equals(request.getConfirmationKey())) {
      throw new RegistrationCompleteException("Wrong email or key");
    }

    user.setIsApproved(true);
    user.setStatus(UserStatus.ACTIVE);
    userRepository.save(user);

    return CommonResponseDTO.<MessageResponseDTO>builder()
        .data(new MessageResponseDTO("ok"))
        .build();
  }

  public CommonResponseDTO<UserDTO> getCurrentUser() {
    var user = CurrentUserUtils.getCurrentUser();

    return CommonResponseDTO.<UserDTO>builder()
        .data(userMapper.userToUserDTO(user))
        .build();
  }
}