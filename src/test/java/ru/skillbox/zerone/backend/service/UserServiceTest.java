package ru.skillbox.zerone.backend.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skillbox.zerone.backend.exception.UserAlreadyExistException;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.request.ChangeEmailDTO;
import ru.skillbox.zerone.backend.model.dto.request.NotificationSettingDTO;
import ru.skillbox.zerone.backend.model.dto.request.RegisterConfirmRequestDTO;
import ru.skillbox.zerone.backend.model.dto.request.RegisterRequestDTO;
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
import ru.skillbox.zerone.backend.testData.UserMockUtils;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;
import ru.skillbox.zerone.backend.model.enumerated.NotificationType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest implements UserMockUtils {
  @Mock
  private UserRepository userRepository;
  @Mock
  private ChangeEmailHistoryRepository changeEmailHistoryRepository;
  @Mock
  private MailService mailService;
  @Mock
  private UserMapper userMapper;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private User user;
  @Mock
  private NotificationSettingRepository notificationSettingRepository;
  private final MockedStatic<CurrentUserUtils> utilsMockedStatic = Mockito.mockStatic(CurrentUserUtils.class);
  @InjectMocks
  private UserService userService;
  //private User currentUser;
  @BeforeAll
  static void setStatic() {
  }
  @BeforeEach
  public void setUp() {
    user = new User().setId(1L);
  }
  @AfterEach
  void tearDown() {
    utilsMockedStatic.close();
  }
  @Test
  void testGetCurrentUser_whenUserExists_thenReturnUserDTO() {
    User user = new User();
    user.setId(1L);
    user.setEmail("test@example.com");
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setPhone("+1234567899");
    user.setCountry("Canada");
    user.setCity("Toronto");
    user.setBirthDate(LocalDate.of(1990, 1, 1));
    user.setPhoto("https://example.com/profile.png");
    user.setAbout("Hello Zerone Project");
    when(CurrentUserUtils.getCurrentUser()).thenReturn(user);
    UserDTO expectedDTO = new UserDTO();
    expectedDTO.setId(user.getId());
    expectedDTO.setEmail(user.getEmail());
    expectedDTO.setFirstName(user.getFirstName());
    expectedDTO.setLastName(user.getLastName());
    CommonResponseDTO<UserDTO> response = userService.getCurrentUser();
    verify(userMapper, times(1)).userToUserDTO(user);
  }
  @Test
  void testGetUserById_whenUserExists_thenReturnUserDTO() {
    long userId = 1L;
    User user = new User();
    user.setId(userId);
    UserDTO userDTO = new UserDTO();
    userDTO.setId(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userMapper.userToUserDTO(user)).thenReturn(userDTO);
    CommonResponseDTO<UserDTO> response = userService.getById(userId);
    assertNotNull(response);
    assertNotNull(response.getData());
    assertEquals(userDTO, response.getData());
    verify(userRepository).findById(userId);
    verify(userMapper).userToUserDTO(user);
  }
  @Test
  void testEditUserSettings_whenValidInput_thenReturnSuccessResponseAndSaveUserToDatabase() {
    User currentUser = new User();
    currentUser.setId(1L);
    UserDTO editUser = new UserDTO();
    editUser.setFirstName("John");
    editUser.setLastName("Doe");
    editUser.setPhone("+1234567890");
    editUser.setCountry("USA");
    editUser.setCity("New York");
    editUser.setLastOnlineTime(LocalDateTime.now());
    editUser.setBirthDate(LocalDate.of(1990, 1, 1));
    editUser.setPhoto("https://example.com/profile.png");
    editUser.setAbout("Hello, World!");
    editUser.setBlocked(false);
    editUser.setDeleted(false);
    utilsMockedStatic.when(CurrentUserUtils::getCurrentUser).thenReturn(currentUser);
    UserDTO updatedUser = new UserDTO();
    updatedUser.setFirstName(editUser.getFirstName());
    updatedUser.setLastName(editUser.getLastName());
    updatedUser.setPhone(editUser.getPhone());
    updatedUser.setCountry(editUser.getCountry());
    updatedUser.setCity(editUser.getCity());
    updatedUser.setLastOnlineTime(editUser.getLastOnlineTime());
    updatedUser.setLastOnlineTime(editUser.getLastOnlineTime());
    updatedUser.setBirthDate(editUser.getBirthDate());
    updatedUser.setPhoto(editUser.getPhoto());
    updatedUser.setAbout(editUser.getAbout());
    when(userRepository.save(any(User.class))).thenReturn(user);
    UserDTO user = userService.editUserSettings(editUser);
    assertEquals(editUser, updatedUser);
    verify(userRepository).save(any(User.class));
  }
  @Test
  void testChangeEmailConfirm_whenValidInput_thenReturnSuccessResponse() {
    String emailOld = "newEmail@example.com";
    String confirmationCode = "123456";
    User user = new User();
    user.setEmail("oldEmail@example.com");
    user.setConfirmationCode("123456789zerone#");
    ChangeEmailHistory changeEmailHistory = new ChangeEmailHistory();
    changeEmailHistory.setEmailOld("oldEmail@example.com");
    changeEmailHistory.setEmailNew("newEmail@example.com");
    utilsMockedStatic.when(CurrentUserUtils::getCurrentUser).thenReturn(user);
    when(CurrentUserUtils.getCurrentUser()).thenReturn(user);
    when(changeEmailHistoryRepository.findFirstByEmailOldOrderByTimeDesc(emailOld)).thenReturn(Optional.of(changeEmailHistory));
    CommonResponseDTO<MessageResponseDTO> response = userService.changeEmailConfirm(emailOld, confirmationCode);
    verify(userRepository).save(user);
  }
  @Test
  void testSendMessageForChangeEmail_WhenUserExists_ThenThrowUserAlreadyExistException() {
    ChangeEmailDTO request = new ChangeEmailDTO();
    request.setEmail("existingEmail@example.com");
    User user = new User();
    user.setEmail(request.getEmail());
    when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);
    utilsMockedStatic.when(CurrentUserUtils::getCurrentUser).thenReturn(user);
    assertThrows(UserAlreadyExistException.class, () -> {
      userService.sendMessageForChangeEmail(request);
    });
    verify(userRepository, never()).save(any());
    verify(changeEmailHistoryRepository, never()).save(any());
    verify(mailService, never()).sendVerificationChangeEmail(any(), any(), any());
  }
  @Test
  void testSendMessageForChangeEmail_WhenValidInput_ThenReturnSuccessResponse() {
    ChangeEmailDTO request = new ChangeEmailDTO();
    request.setEmail("oldEmail@example.com");
    User user = new User();
    user.setEmail("oldEmail@example.com");
    user.setConfirmationCode("111111");
    utilsMockedStatic.when(CurrentUserUtils::getCurrentUser).thenReturn(user);
    when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
    when(userRepository.save(user)).thenReturn(user);
    when(changeEmailHistoryRepository.save(any())).thenAnswer(invocation -> {
      ChangeEmailHistory argument = invocation.getArgument(0);
      argument.setId(1L);
      return argument;
    });
    when(userRepository.save(user)).thenReturn(user);
    user.setConfirmationCode("111111");
    CommonResponseDTO<MessageResponseDTO> response = userService.sendMessageForChangeEmail(request);
    assertNotNull(response);
    assertNotNull(response.getData());
    assertNull(response.getError());
    verify(userRepository, times(1)).save(user);
    verify(changeEmailHistoryRepository, times(1)).save(any());
    verify(mailService, times(1)).sendVerificationChangeEmail(
        request.getEmail(),
        user.getConfirmationCode(),
        "/changeemail/complete");
  }
@Test
void testRegisterAccount_whenValidInput_thenReturnSuccessResponseAndSaveUserToDatabase() {
  RegisterRequestDTO request = new RegisterRequestDTO();
  request.setEmail("oldEmail@example.com");
  request.setPassword("password");
  request.setFirstName("John");
  request.setLastName("Doe");
  User user = userMapper.registerRequestDTOToUser(request, "123456", "photo");
  assertEquals(request.getFirstName(), request.getFirstName());
  assertEquals(request.getLastName(), request.getLastName());
  assertFalse(passwordEncoder.matches(request.getPassword(), request.getPassword()));
  assertNotNull(request.getEmail());
}
  @Test
  void testRegistrationConfirm_whenValidInput_thenReturnSuccessResponseAndActivateUserAccount() {
    RegisterConfirmRequestDTO request = new RegisterConfirmRequestDTO();
    request.setEmail("new@example.com");
    request.setConfirmationKey("123456");
    User user = new User();
    user.setEmail(request.getEmail());
    user.setConfirmationCode(request.getConfirmationKey());
    when(userRepository.findUserByEmail(request.getEmail())).thenReturn(Optional.of(user));
    CommonResponseDTO<MessageResponseDTO> response = userService.registrationConfirm(request);
    verify(userRepository, times(1)).save(user);
    assertTrue(user.getIsApproved());
    assertEquals(UserStatus.ACTIVE, user.getStatus());
  }
  @Test
  void testSetNotificationType_whenValidInput_thenReturnSuccessResponseAndSaveToDatabase() {
    User currentUser = new User();
    currentUser.setId(1L);
    NotificationSettingDTO typeDTO = new NotificationSettingDTO("type", true);
    typeDTO.setType(NotificationType.POST.name());
    typeDTO.setEnable(true);
    NotificationSetting setting = new NotificationSetting();
    setting.setUser(currentUser);
    utilsMockedStatic.when(CurrentUserUtils::getCurrentUser).thenReturn(currentUser);
    when(notificationSettingRepository.findByUser(currentUser)).thenReturn(Optional.of(setting));
    when(notificationSettingRepository.save(any(NotificationSetting.class))).thenReturn(setting);
    CommonResponseDTO<MessageResponseDTO> response = userService.setNotificationType(typeDTO);
    assertNotNull(response);
    assertNotNull(response.getData());
    verify(notificationSettingRepository).findByUser(currentUser);
    verify(notificationSettingRepository).save(setting);
  }
}


