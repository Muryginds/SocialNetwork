package ru.skillbox.zerone.backend.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.configuration.MailServiceConfig;
import ru.skillbox.zerone.backend.exception.ChangeEmailException;
import ru.skillbox.zerone.backend.exception.RegistrationCompleteException;
import ru.skillbox.zerone.backend.exception.UserAlreadyExistException;
import ru.skillbox.zerone.backend.exception.UserNotFoundException;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.request.*;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;
import ru.skillbox.zerone.backend.model.entity.ChangeEmailHistory;
import ru.skillbox.zerone.backend.model.entity.NotificationSetting;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.model.enumerated.NotificationType;
import ru.skillbox.zerone.backend.model.enumerated.UserStatus;
import ru.skillbox.zerone.backend.repository.ChangeEmailHistoryRepository;
import ru.skillbox.zerone.backend.repository.NotificationSettingRepository;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;
import ru.skillbox.zerone.backend.util.ResponseUtils;

import java.io.File;
import java.net.InetAddress;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final ChangeEmailHistoryRepository changeEmailHistoryRepository;
  private final MailService mailService;
  private final FriendsService friendsService;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final MailServiceConfig mailServiceConfig;
  private final NotificationSettingRepository notificationSettingRepository;
  @Autowired
  private HttpServletRequest request;


  public CommonResponseDTO<MessageResponseDTO> changePassword(ChangePasswordDTO request) {
    var user = CurrentUserUtils.getCurrentUser();
    var password = request.getPassword();

    user.setPassword(passwordEncoder.encode(password));
    userRepository.save(user);

    return ResponseUtils.commonResponseDataOk();
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

    return ResponseUtils.commonResponseDataOk();
  }

  @Transactional
  public CommonResponseDTO<MessageResponseDTO> changeEmailConfirm(String emailOld, String confirmationCode) {

    User user = CurrentUserUtils.getCurrentUser();
    String token = user.getConfirmationCode();

    if (token.equals(confirmationCode)) {
      throw new ChangeEmailException(confirmationCode, emailOld);
    }

    if (user.getEmail().equals(emailOld)) {
      throw new ChangeEmailException(confirmationCode, emailOld);
    }

    ChangeEmailHistory changeEmailHistory = changeEmailHistoryRepository.findFirstByEmailOldOrderByTimeDesc(emailOld)
        .orElseThrow(() -> new RegistrationCompleteException(String.format("Заявка на смену email %s не была найдена в базе", emailOld)));

    String newEmail = changeEmailHistory.getEmailNew();

    user.setEmail(newEmail);
    userRepository.save(user);

    return ResponseUtils.commonResponseDataOk();
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


    return ResponseUtils.commonResponseDataOk();
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
    user.setCity(getClientIpAddress());
    user.setStatus(UserStatus.ACTIVE);
    userRepository.save(user);
    friendsService.createPersonalRecommendations(user);


    return ResponseUtils.commonResponseDataOk();
  }

  public CommonResponseDTO<UserDTO> getCurrentUser() {
    var user = CurrentUserUtils.getCurrentUser();

    return ResponseUtils.commonResponseWithData(userMapper.userToUserDTO(user));
  }

  public CommonResponseDTO<UserDTO> getById(Long id) {
    User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

    return ResponseUtils.commonResponseWithData(userMapper.userToUserDTO(user));
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

 @Transactional
  public CommonResponseDTO<MessageResponseDTO> setNotificationType(NotificationTypeDTO typeDTO) {
    User user = CurrentUserUtils.getCurrentUser();
    NotificationType notificationType = NotificationType.valueOf(typeDTO.getType());
    boolean enabled = typeDTO.getEnable();
   Optional<NotificationSetting> optionalSetting = notificationSettingRepository.findByUser(user);
   NotificationSetting setting;
   if (optionalSetting.isPresent()) {
     setting = optionalSetting.get();
   } else {
     setting = new NotificationSetting();
     setting.setUser(user);
   }
   switch (notificationType) {
      case POST -> setting.setPostEnabled(enabled);
      case POST_COMMENT -> setting.setPostCommentEnabled(enabled);
      case COMMENT_COMMENT -> setting.setCommentCommentEnabled(enabled);
      case FRIEND_REQUEST -> setting.setFriendRequestEnabled(enabled);
      case MESSAGE -> setting.setMessagesEnabled(enabled);
      case FRIEND_BIRTHDAY -> setting.setFriendBirthdayEnabled(enabled);
    }
    notificationSettingRepository.save(setting);
    return ResponseUtils.commonResponseDataOk();
  }

  public CommonResponseDTO<UserDTO> deleteUser() {
    var user = CurrentUserUtils.getCurrentUser();
    user.setIsDeleted(true);
    userRepository.save(user);
    return ResponseUtils.commonResponseWithData(userMapper.userToUserDTO(user));
  }
  private String getClientIpAddress()  {
    try {
      String ipAddress = request.getHeader("X-FORWARDED-FOR");
      if (ipAddress == null) {
        ipAddress = request.getRemoteAddr();
      }
      File database = new File("src/main/resources/GeoIP/GeoLite2-City.mmdb");
      DatabaseReader dbReader = new DatabaseReader.Builder(database).build();
      CityResponse response = dbReader.city(InetAddress.getByName(ipAddress));

      return response.getCity().getName();

    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }
}