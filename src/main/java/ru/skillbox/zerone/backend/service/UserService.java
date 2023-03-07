package ru.skillbox.zerone.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.configuration.MailServiceConfig;
import ru.skillbox.zerone.backend.exception.RegistrationCompleteException;
import ru.skillbox.zerone.backend.exception.UserAlreadyExistException;
import ru.skillbox.zerone.backend.exception.UserNotFoundException;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.request.ChangeEmailDTO;
import ru.skillbox.zerone.backend.model.dto.request.ChangePasswordDTO;
import ru.skillbox.zerone.backend.model.dto.request.RegisterConfirmRequestDTO;
import ru.skillbox.zerone.backend.model.dto.request.RegisterRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;
import ru.skillbox.zerone.backend.model.entity.ChangeEmailHistory;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.model.enumerated.UserStatus;
import ru.skillbox.zerone.backend.repository.ChangeEmailHistoryRepository;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;
import ru.skillbox.zerone.backend.util.ResponseUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final ChangeEmailHistoryRepository changeEmailHistoryRepository;
  private final MailService mailService;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final MailServiceConfig mailServiceConfig;


  public CommonResponseDTO<MessageResponseDTO> changePassword(ChangePasswordDTO request) {
    var user = CurrentUserUtils.getCurrentUser();
    var password = request.getPassword();

    user.setPassword(passwordEncoder.encode(password));
    userRepository.save(user);

    return ResponseUtils.commonResponseOk();
  }

  @Transactional
  public CommonResponseDTO<MessageResponseDTO> sendMessageForChangeEmail(ChangeEmailDTO request) {

    User user = CurrentUserUtils.getCurrentUser();

    String emailOld = user.getEmail();

    ChangeEmailHistory changeEmailHistory = ChangeEmailHistory.builder()
        .emailOld(emailOld)
        .emailNew(request.getEmail())
        .build();

    if (userRepository.existsByEmail(request.getEmail())) {
      throw new UserAlreadyExistException(user.getEmail());
    }

    var verificationUuid = UUID.randomUUID();
    user.setConfirmationCode(verificationUuid.toString());
    userRepository.save(user);
    changeEmailHistoryRepository.save(changeEmailHistory);

    mailService.sendVerificationChangeEmail(
        emailOld,
        user.getConfirmationCode(),
        "/changeemail/complete",
        mailServiceConfig.getServerAddress());

    return ResponseUtils.commonResponseOk();
  }

  @Transactional
  public CommonResponseDTO<MessageResponseDTO> changeEmailConfirm(String emailOld, String confirmationCode) {

    User user = CurrentUserUtils.getCurrentUser();
    String token = user.getConfirmationCode();

    var changeEmailHistoryOptional = changeEmailHistoryRepository.findFirstByEmailOldOrderByTimeDesc(emailOld);

    if (changeEmailHistoryOptional.isEmpty()) {
      throw new RegistrationCompleteException("Email dont find in DB");
    }

    ChangeEmailHistory changeEmailHistory = changeEmailHistoryOptional.get();

    String newEmail = changeEmailHistory.getEmailNew();

    if (token.equals(confirmationCode) && user.getEmail().equals(emailOld)) {
      user.setEmail(newEmail);
      userRepository.save(user);

      return ResponseUtils.commonResponseOk();
    } else {
      return CommonResponseDTO.<MessageResponseDTO>builder()
          .data(new MessageResponseDTO("error"))
          .build();
    }
  }

  @Transactional
  public CommonResponseDTO<MessageResponseDTO> registerAccount(RegisterRequestDTO request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new UserAlreadyExistException(request.getEmail());
    }

    User user = userMapper.registerRequestDTOToUser(request);
    var verificationUuid = UUID.randomUUID();
    user.setConfirmationCode(verificationUuid.toString());

    userRepository.save(user);

    mailService.sendVerificationEmail(
        user.getEmail(),
        verificationUuid.toString(),
        "/registration/complete",
        mailServiceConfig.getFrontAddress());

    return ResponseUtils.commonResponseOk();
  }

  @Transactional
  public CommonResponseDTO<MessageResponseDTO> registrationConfirm(RegisterConfirmRequestDTO request) {
    var user = userRepository.findUserByEmail(request.getEmail())
        .orElseThrow(() -> new RegistrationCompleteException("Wrong email or key"));

    if (Boolean.TRUE.equals(user.getIsApproved())) {
      throw new RegistrationCompleteException("User already confirmed");
    }

    if (!user.getConfirmationCode().equals(request.getConfirmationKey())) {
      throw new RegistrationCompleteException("Wrong email or key");
    }

    user.setIsApproved(true);
    user.setStatus(UserStatus.ACTIVE);
    userRepository.save(user);

    return ResponseUtils.commonResponseOk();
  }

  public CommonResponseDTO<UserDTO> getCurrentUser() {
    var user = CurrentUserUtils.getCurrentUser();

    return CommonResponseDTO.<UserDTO>builder()
        .data(userMapper.userToUserDTO(user))
        .build();
  }

  public CommonResponseDTO<UserDTO> getById(Long id) {
    User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    UserDTO userDto = userMapper.userToUserDTO(user);
    return CommonResponseDTO.<UserDTO>builder().data(userDto).build();

  }

  public UserDTO editUserSettings(UserDTO editUser) {
    User user = CurrentUserUtils.getCurrentUser();
    user.setFirstName(editUser.getFirstName())
        .setLastName(editUser.getLastName())
        .setPhone(editUser.getPhone())
        .setCountry(editUser.getCountry())
        .setCity(editUser.getCity())
        .setBirthDate(editUser.getBirthDate())
        .setPhoto(editUser.getPhoto())
        .setAbout(editUser.getAbout());
    userRepository.save(user);
    return userMapper.userToUserDTO(user);
  }
}