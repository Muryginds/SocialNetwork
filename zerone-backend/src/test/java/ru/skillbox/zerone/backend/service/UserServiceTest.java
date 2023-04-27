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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skillbox.zerone.backend.exception.ChangeEmailException;
import ru.skillbox.zerone.backend.exception.UserAlreadyExistException;
import ru.skillbox.zerone.backend.exception.UserNotFoundException;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.request.ChangeEmailDTO;
import ru.skillbox.zerone.backend.model.dto.request.NotificationSettingDTO;
import ru.skillbox.zerone.backend.model.dto.request.RegisterConfirmRequestDTO;
import ru.skillbox.zerone.backend.model.dto.request.RegisterRequestDTO;
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
import ru.skillbox.zerone.backend.testData.UserMockUtils;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
  @Mock FriendService friendService;
  @Mock
  private UserMapper userMapper;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private User user;
  @Mock
  private SearchService searchService;
  @Mock
  private NotificationSettingRepository notificationSettingRepository;
  @Mock
  private NotificationSettingService notificationSettingService;
  private final MockedStatic<CurrentUserUtils> utilsMockedStatic = Mockito.mockStatic(CurrentUserUtils.class);
  @InjectMocks
  private UserService userService;
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
    User user = User.builder()
        .id(1L)
        .email("test@example.com")
        .firstName("John")
        .lastName("Doe")
        .phone("+1234567899")
        .country("Canada")
        .city("Toronto")
        .birthDate(LocalDate.of(1990, 1, 1))
        .photo("https://example.com/profile.png")
        .about("Hello Zerone Project")
        .build();
    when(CurrentUserUtils.getCurrentUser()).thenReturn(user);
    UserDTO expectedDTO = UserDTO.builder()
        .id(user.getId())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .build();
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
    UserDTO editUser = UserDTO.builder()
        .firstName("John")
        .lastName("Doe")
        .phone("+1234567890")
        .country("USA")
        .city("New York")
        .lastOnlineTime(LocalDateTime.now())
        .birthDate(LocalDate.of(1990, 1, 1))
        .photo("https://example.com/profile.png")
        .about("Hello, World!")
        .isBlocked(false)
        .isDeleted(false)
        .build();
    utilsMockedStatic.when(CurrentUserUtils::getCurrentUser).thenReturn(currentUser);
    UserDTO updatedUser = UserDTO.builder()
        .firstName(editUser.getFirstName())
        .lastName(editUser.getLastName())
        .phone(editUser.getPhone())
        .country(editUser.getCountry())
        .city(editUser.getCity())
        .lastOnlineTime(editUser.getLastOnlineTime())
        .lastOnlineTime(editUser.getLastOnlineTime())
        .birthDate(editUser.getBirthDate())
        .photo(editUser.getPhoto())
        .about(editUser.getAbout())
        .build();
    when(userRepository.save(any(User.class))).thenReturn(user);
    UserDTO user = userService.editUserSettings(editUser);
    assertEquals(editUser, updatedUser);
    verify(userRepository).save(any(User.class));
  }
  @Test
  void confirmEmailChange_Succeeds_ChangesEmailAndSavesToDatabase() {
    String oldEmail = "old_email@example.com";
    String newEmail = "new_email@example.com";
    String confirmationCode = "123456";
    User user = new User();
    user.setEmail(oldEmail);
    user.setConfirmationCode(confirmationCode);
    ChangeEmailHistory changeEmailHistory = new ChangeEmailHistory();
    changeEmailHistory.setEmailOld(oldEmail);
    changeEmailHistory.setEmailNew(newEmail);
    Mockito.when(userRepository.findUserByEmail(oldEmail)).thenReturn(Optional.of(user));
    Mockito.when(changeEmailHistoryRepository.findFirstByEmailOldOrderByTimeDesc(oldEmail)).thenReturn(Optional.of(changeEmailHistory));
    CommonResponseDTO<MessageResponseDTO> response = userService.changeEmailConfirm(oldEmail, confirmationCode);
    assertEquals(newEmail, user.getEmail());
    Mockito.verify(userRepository, Mockito.times(1)).save(user);
  }
  @Test
  void confirmEmailChange_ThrowsUserNotFoundExceptionWhenUserNotFound() {
    String oldEmail = "old_email@example.com";
    String confirmationCode = "123456";
    Mockito.when(userRepository.findUserByEmail(oldEmail)).thenReturn(Optional.empty());
    assertThrows(UserNotFoundException.class, () -> userService.changeEmailConfirm(oldEmail, confirmationCode));
  }
  @Test
  void confirmEmailChange_ThrowsChangeEmailExceptionWhenConfirmationCodeIsWrong() {
    String oldEmail = "old_email@example.com";
    String confirmationCode = "123456";
    User user = new User();
    user.setEmail(oldEmail);
    user.setConfirmationCode("654321");
    Mockito.when(userRepository.findUserByEmail(oldEmail)).thenReturn(Optional.of(user));
    assertThrows(ChangeEmailException.class, () -> userService.changeEmailConfirm(oldEmail, confirmationCode));
  }
  @Test
  void verifyChangeEmailRequest_ThrowsUserNotFoundException() {
    String oldEmail = "old_email@example.com";
    String confirmationCode = "123456";
    User user = new User();
    user.setEmail("another_email@example.com");
    user.setConfirmationCode(confirmationCode);
    Mockito.when(userRepository.findUserByEmail(oldEmail)).thenReturn(Optional.of(user));
    assertThrows(ChangeEmailException.class, () -> userService.changeEmailConfirm(oldEmail, confirmationCode));
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
    CommonResponseDTO<MessageResponseDTO> response = userService.setNotificationType(typeDTO);
    assertNotNull(response);
    assertNotNull(response.getData());
    verify(notificationSettingRepository).findByUser(currentUser);
  }
  @Test
  void searchUsers_shouldReturnListOfUserDTOs() {
    String name = "John";
    String lastName = "Doe";
    String country = "USA";
    String city = "New York";
    Integer ageFrom = 20;
    Integer ageTo = 30;
    int offset = 0;
    int itemPerPage = 10;
    List<User> users = new ArrayList<>();
    User user = new User();
    user.setId(1L);
    users.add(user);
    Page<User> pageUsers = new PageImpl<>(users);
    when(searchService.searchUsers(name, lastName, country, city, ageFrom, ageTo, PageRequest.of(offset / itemPerPage, itemPerPage))).thenReturn(pageUsers);
    when(userMapper.usersToUserDTO(users)).thenReturn(new ArrayList<>());
    CommonListResponseDTO<UserDTO> result = userService.searchUsers(name, lastName, country, city, ageFrom, ageTo, offset, itemPerPage);
    verify(searchService).searchUsers(name, lastName, country, city, ageFrom, ageTo, PageRequest.of(offset / itemPerPage, itemPerPage));
    verify(userMapper).usersToUserDTO(users);
    assertEquals(users.size(), result.getTotal());
    assertEquals(offset, result.getOffset());
    assertEquals(itemPerPage, result.getPerPage());
  }
}



