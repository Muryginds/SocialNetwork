package ru.skillbox.zerone.backend.service;

import com.kuliginstepan.dadata.client.DadataClient;
import com.kuliginstepan.dadata.client.domain.Suggestion;
import com.kuliginstepan.dadata.client.domain.address.Address;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.exception.ChangeEmailException;
import ru.skillbox.zerone.backend.exception.RegistrationCompleteException;
import ru.skillbox.zerone.backend.exception.UserAlreadyExistException;
import ru.skillbox.zerone.backend.exception.UserNotFoundException;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.request.*;
import ru.skillbox.zerone.backend.model.dto.response.CommonListResponseDTO;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.skillbox.zerone.backend.model.enumerated.NotificationType.*;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final ChangeEmailHistoryRepository changeEmailHistoryRepository;
  private final MailService mailService;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final NotificationSettingRepository notificationSettingRepository;
  private final SearchService searchService;
  private final StorageService storageService;
  private final FriendService friendService;
  private final DadataClient client;
  @Value("${ip-check.url}")
  String urlString;


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
        "/changeemail/complete");

    return ResponseUtils.commonResponseDataOk();
  }

  @Transactional
  public CommonResponseDTO<MessageResponseDTO> changeEmailConfirm(String emailOld, String confirmationCode) {

    var user = userRepository.findUserByEmail(emailOld)
        .orElseThrow(() -> new UserNotFoundException(emailOld));
    String confirmationToken = user.getConfirmationCode();

    if (!(confirmationToken.equals(confirmationCode))) {
      throw new ChangeEmailException(confirmationCode, emailOld);
    }

    if (!(user.getEmail().equals(emailOld))) {
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

    var confirmationCode = UUID.randomUUID().toString();
    var photoUrl = storageService.generateStartAvatarUrl();
    var user = userMapper.registerRequestDTOToUser(request, confirmationCode, photoUrl);

    userRepository.save(user);

    mailService.sendVerificationEmail(
        user.getEmail(),
        confirmationCode,
        "/registration/complete");

    return ResponseUtils.commonResponseDataOk();
  }

  @Transactional
  public CommonResponseDTO<MessageResponseDTO> registrationConfirm(RegisterConfirmRequestDTO request) {
    var user = userRepository.findUserByEmail(request.getEmail())
        .orElseThrow(() -> new RegistrationCompleteException("Указан неверный адрес почты или ключ подтверждения"));

    if (Boolean.TRUE.equals(user.getIsApproved())) {
      throw new RegistrationCompleteException("Учетная запись уже подтверждена");
    }

    if (!user.getConfirmationCode().equals(request.getConfirmationKey())) {
      throw new RegistrationCompleteException("Указан неверный адрес почты или ключ подтверждения");
    }

    user.setIsApproved(true);
    try {
      user.setCity(getCity(getClientIpAddress()));
      user.setCountry(getCountry(getClientIpAddress()));
    } catch (IOException e) {
      user.setCity("");
      user.setCountry("");
    }
    user.setStatus(UserStatus.ACTIVE);
    userRepository.save(user);
    friendService.createPersonalRecommendations(user);

    return ResponseUtils.commonResponseDataOk();
  }

  public CommonResponseDTO<UserDTO> getCurrentUser() {
    var user = CurrentUserUtils.getCurrentUser();

    return ResponseUtils.commonResponseWithData(userMapper.userToUserDTO(user));
  }

  public CommonResponseDTO<UserDTO> getById(long id) {
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

  @SuppressWarnings("java:S107")
  public CommonListResponseDTO<UserDTO> searchUsers(String name, String lastName, String country, String city, Integer ageFrom, Integer ageTo, int offset, int itemPerPage) {
    Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
    var pageUsers = searchService.searchUsers(name, lastName, country, city, ageFrom, ageTo, pageable);

    return CommonListResponseDTO.<UserDTO>builder()
        .total(pageUsers.getTotalElements())
        .offset(offset)
        .perPage(itemPerPage)
        .data(userMapper.usersToUserDTO(pageUsers.getContent()))
        .build();
  }

  @Transactional
  public CommonResponseDTO<MessageResponseDTO> setNotificationType(NotificationSettingDTO typeDTO) {
    var setting = getNotificationSetting();
    boolean enabled = typeDTO.getEnable();
    NotificationType notificationType = NotificationType.valueOf(typeDTO.getType());
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

  public CommonListResponseDTO<NotificationSettingDTO> getNotificationSettingList() {
    var setting = getNotificationSetting();
    List<NotificationSettingDTO> data = new ArrayList<>();
    addSetting(data, POST, setting.getPostEnabled());
    addSetting(data, POST_COMMENT, setting.getPostCommentEnabled());
    addSetting(data, COMMENT_COMMENT, setting.getCommentCommentEnabled());
    addSetting(data, FRIEND_REQUEST, setting.getFriendRequestEnabled());
    addSetting(data, MESSAGE, setting.getMessagesEnabled());
    addSetting(data, FRIEND_BIRTHDAY, setting.getFriendBirthdayEnabled());

    return CommonListResponseDTO.<NotificationSettingDTO>builder()
        .data(data).build();
  }

  private NotificationSetting getNotificationSetting() {
    User user = CurrentUserUtils.getCurrentUser();
    Optional<NotificationSetting> optionalSetting = notificationSettingRepository.findByUser(user);
    NotificationSetting setting;
    if (optionalSetting.isPresent()) {
      setting = optionalSetting.get();
    } else {
      setting = new NotificationSetting();
      setting.setUser(user);
    }
    return setting;
  }

  private void addSetting(List<NotificationSettingDTO> data, NotificationType type, boolean enabled) {
    var setting = new NotificationSettingDTO(type.name(), enabled);
    data.add(setting);
  }

  public CommonResponseDTO<Object> deleteUser() {
    var user = CurrentUserUtils.getCurrentUser();
    user.setIsDeleted(true);
    userRepository.save(user);
    return CommonResponseDTO.builder()
        .build();
  }

  private String getClientIpAddress() throws IOException {

    URL url = new URL(urlString);
    try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
      return br.readLine();
    }

  }


  private String getCity(String ip) {
    Suggestion<Address> address = client.iplocate(ip).block();
    if (address == null) {
      return "";
    }

    return address.getData().getCity();
  }

  private String getCountry(String ip) {
    Suggestion<Address> address = client.iplocate(ip).block();
    if (address == null) {
      return "";
    }

    return address.getData().getCountry();

  }

  public CommonResponseDTO<MessageResponseDTO> registrationComplete() {
    return ResponseUtils.commonResponseDataOk();
  }
}
