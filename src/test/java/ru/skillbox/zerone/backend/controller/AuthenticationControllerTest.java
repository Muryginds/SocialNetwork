package ru.skillbox.zerone.backend.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.skillbox.zerone.backend.AbstractIntegrationTest;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.service.RoleService;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
class AuthenticationControllerTest extends AbstractIntegrationTest {
  private static final String TEST_ACCOUNT_EMAIL = "testAccount@hotmail.com";
  private static final String TEST_ACCOUNT_FIRSTNAME = "Серж";
  private static final String TEST_ACCOUNT_LASTNAME = "Богданов";
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private RoleService roleService;
  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    var user = User.builder()
        .email(TEST_ACCOUNT_EMAIL)
        .firstName(TEST_ACCOUNT_FIRSTNAME)
        .lastName(TEST_ACCOUNT_LASTNAME)
        .password(passwordEncoder.encode(TEST_ACCOUNT_EMAIL))
        .isApproved(true)
        .confirmationCode("test")
        .roles(List.of(roleService.getBasicUserRole()))
        .build();
    userRepository.save(user);
  }

  @Test
  void testLogin_whenValidInput_thenReturnSuccessResponse() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(MessageFormatter.format("""
                    {
                      "email": "{}",
                      "password": "{}"
                    }
                """, TEST_ACCOUNT_EMAIL, TEST_ACCOUNT_EMAIL)
                .getMessage()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.first_name").value(TEST_ACCOUNT_FIRSTNAME))
        .andExpect(jsonPath("$.data.last_name").value(TEST_ACCOUNT_LASTNAME))
        .andExpect(jsonPath("$.data.email").value(TEST_ACCOUNT_EMAIL))
        .andExpect(jsonPath("$.data.token").isNotEmpty());
  }

  @Test
  void testLogin_whenWrongPassword_thenReturnClientError() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(MessageFormatter.format("""
                    {
                      "email": "{}",
                      "password": "wrong-password"
                    }
                """, TEST_ACCOUNT_EMAIL)
                .getMessage()))
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.error").value("Неверный пароль или имя пользователя"))
        .andExpect(jsonPath("$.data.token").doesNotExist());
  }

  @Test
  void testLogin_whenAccountNotExist_thenReturn400Error() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                      "email": "wrong-account@test.ru",
                      "password": "wrong-password"
                    }
                """))
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.error").value("Пользователь с почтой: wrong-account@test.ru не найден"))
        .andExpect(jsonPath("$.data").doesNotExist());
  }

  @Test
  void testLogin_whenEmailNotValid_thenReturnServerError() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                      "email": "invalidEmail",
                      "password": "wrong-password"
                    }
                """))
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.error").isNotEmpty())
        .andExpect(jsonPath("$.data").doesNotExist());
  }

/*  @Test
  void testLogout_whenValidToken_thenReturnSuccessResponse() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/logout")
            .header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(content().json("""
            {
              "data": {"message": "OK"}
            }
            """));
  }*/
}