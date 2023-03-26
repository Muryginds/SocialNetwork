package ru.skillbox.zerone.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import ru.skillbox.zerone.backend.exception.UserNotFoundException;
import ru.skillbox.zerone.backend.mapstruct.UserMapper;
import ru.skillbox.zerone.backend.model.dto.request.AuthRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.security.JwtTokenProvider;
import ru.skillbox.zerone.backend.testData.UserMockUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest implements UserMockUtils {
  private static final String EMAIL = "test@example.com";
  private static final String PASSWORD = "password";
  private static final String WRONG_PASSWORD = "wrongpassword";
  @Mock
  private AuthenticationManager authenticationManager;
  @Mock
  private JwtTokenProvider jwtTokenProvider;
  @Mock
  private UserRepository userRepository;
  @Mock
  private UserMapper userMapper;
  @InjectMocks
  private LoginService loginService;
  private User user;
  private AuthRequestDTO request;

  @BeforeEach
  public void setUp() {
    user = getTestUser(EMAIL, PASSWORD);
    request = getAuthRequest();
  }

  @Test
  void testLogin_whenValidUser_thenReturnUserDTOWithToken() {
    var testToken = "token";
    when(userRepository.findUserByEmail(request.getEmail())).thenReturn(Optional.of(user));

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);

    when(jwtTokenProvider.createToken(user.getEmail(), user.getRoles())).thenReturn(testToken);

    UserDTO userDTO = new UserDTO();
    userDTO.setEmail(user.getEmail());
    userDTO.setToken(testToken);
    when(userMapper.userToUserDTO(user, testToken)).thenReturn(userDTO);

    CommonResponseDTO<UserDTO> response = loginService.login(request);
    assertEquals(userDTO, response.getData());
    assertEquals(testToken, response.getData().getToken());
    assertNull(response.getError());
  }

  @Test
  void testLogin_whenUserNotFound_thenThrowUserNotFoundException() {
    when(userRepository.findUserByEmail(request.getEmail())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> loginService.login(request));
  }

  @Test
  void testLogin_whenUserWithBadCredentials_thenThrowBadCredentialsException() {
    request.setPassword(WRONG_PASSWORD);

    when(userRepository.findUserByEmail(request.getEmail())).thenReturn(Optional.of(user));

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("Неверный логин или пароль"));

    assertThrows(BadCredentialsException.class, () -> loginService.login(request));
  }

  private AuthRequestDTO getAuthRequest() {
    return AuthRequestDTO.builder()
        .email(EMAIL)
        .password(PASSWORD)
        .build();
  }
}