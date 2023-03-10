package ru.skillbox.zerone.backend.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.skillbox.zerone.backend.model.dto.request.AuthRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.MessageResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;
import ru.skillbox.zerone.backend.service.LoginService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

  @Mock
  private LoginService loginService;

  @InjectMocks
  private AuthenticationController authenticationController;

  @Test
  void testLogin_whenValidInput_thenReturnSuccessResponse() throws Exception {
    AuthRequestDTO requestDTO = new AuthRequestDTO("john.doe@example.com", "password");
    UserDTO userDTO = new UserDTO();
    userDTO.setEmail("john.doe@example.com");
    userDTO.setFirstName("John");
    userDTO.setLastName("Doe");

    when(loginService.login(requestDTO)).thenReturn(
        CommonResponseDTO.<UserDTO>builder()
            .data(userDTO)
            .build()
    );

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();

    mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\": \"john.doe@example.com\", \"password\": \"password\"}"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(
            "{\"data\": {\"first_name\": \"John\", \"last_name\": " +
                "\"Doe\", \"email\": \"john.doe@example.com\"}}"));

    verify(loginService, times(1)).login(requestDTO);
    verifyNoMoreInteractions(loginService);
  }

  @Test
  void testLogout_whenValidToken_thenReturnSuccessResponse() throws Exception {
    String token = "Bearer abc123";

    when(loginService.logout(token)).thenReturn(
        CommonResponseDTO.<MessageResponseDTO>builder()
            .data(new MessageResponseDTO("OK"))
            .build()
    );

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();

    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/logout")
            .header("Authorization", token))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(
            "{\"data\": {\"message\": \"OK\"}}")
        );

    verify(loginService, times(1)).logout(token);
    verifyNoMoreInteractions(loginService);
  }
}