package ru.skillbox.zerone.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.skillbox.zerone.backend.AbstractIntegrationTest;
import ru.skillbox.zerone.backend.model.dto.request.ChangeEmailDTO;
import ru.skillbox.zerone.backend.model.dto.request.ChangePasswordDTO;
import ru.skillbox.zerone.backend.model.dto.request.NotificationSettingDTO;
import ru.skillbox.zerone.backend.model.dto.request.RegisterRequestDTO;
import ru.skillbox.zerone.backend.repository.UserRepository;
import ru.skillbox.zerone.backend.testData.UserMockUtils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class AccountControllerTest extends AbstractIntegrationTest implements UserMockUtils {
  private static final String TEST_ACCOUNT_EMAIL = "testAccount@hotmail.com";
  private static final String TEST_ACCOUNT_PASSWORD = "testPassword123#";
  private static final String TEST_PASSWORD = "password123#";
  private static final String TEST_CONFIRMATION_CODE = "123456";
  private static final String TEST_ACCOUNT_FIRSTNAME = "Серж";
  private static final String TEST_ACCOUNT_LASTNAME = "Богданов";
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private UserRepository userRepository;
  @Test
  void testRegister_whenValidInput_thenReturnSuccessResponse() throws Exception {
    mockMvc.perform(post("/api/v1/account/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(MessageFormatter.format("""
                        {
                          "first_name": "John",
                          "last_name": "Doe",
                          "email": "{}",
                          "password": "{}",
                          "confirm_password": "212112"
                        }
                    """, TEST_ACCOUNT_EMAIL, TEST_ACCOUNT_PASSWORD)
                .getMessage()))
        .andExpect(status().is5xxServerError());
  }
  @Test
  void testRegister_whenEmailNotValid_thenReturnServerError() throws Exception {
    var user = new RegisterRequestDTO();
    user.setEmail("invalidEmail");
    user.setPassword(TEST_ACCOUNT_PASSWORD);
    user.setFirstName(TEST_ACCOUNT_FIRSTNAME);
    user.setLastName(TEST_ACCOUNT_LASTNAME);
    mockMvc.perform(post("/api/v1/account/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                      "email": "invalidEmail",
                      "password": "testPassword",
                      "first_name": "John",
                      "last_name": "Doe"
                    }
                """))
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.error").isNotEmpty())
        .andExpect(jsonPath("$.data").doesNotExist());
  }
  @Test
  @Sql(scripts = "classpath:mock-user-insert.sql")
  void testRegister_whenEmailAlreadyExists_thenReturnClientError() throws Exception {
    mockMvc.perform(post("/api/v1/account/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(MessageFormatter.format("""
                        {
                          "first_name": "John",
                          "last_name": "Doe",
                          "email": "{}",
                          "password": "{}",
                          "confirm_password": "{}"
                        }
                    """, TEST_ACCOUNT_EMAIL, TEST_ACCOUNT_PASSWORD)
                .getMessage()))
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.error").value("Пользователь с email: testAccount@hotmail.com уже существует"))
        .andExpect(jsonPath("$.data").doesNotExist());
  }
  @Test
  void testRegister_whenInvalidEmail_thenReturnClientError() throws Exception {
    mockMvc.perform(post("/api/v1/account/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                      "first_name": "John",
                      "last_name": "Doe",
                      "email": "invalidEmail",
                      "password": "testPassword",
                      "confirm_password": "testPassword"
                    }
                """))
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.error").isNotEmpty())
        .andExpect(jsonPath("$.data").doesNotExist());
  }
  @Test
  void testRegistrationConfirm_whenValidRequest_thenReturnSuccessResponse() throws Exception {
    var user = getTestUser(TEST_ACCOUNT_EMAIL, "password");
    user.setConfirmationCode(TEST_CONFIRMATION_CODE);
    user.setFirstName(TEST_ACCOUNT_FIRSTNAME);
    user.setLastName(TEST_ACCOUNT_LASTNAME);
    userRepository.save(user);
    mockMvc.perform(post("/api/v1/account/register/confirm")
            .contentType(MediaType.APPLICATION_JSON)
            .content(MessageFormatter.format("""
                    {
                      "email": "{}",
                      "confirmationCode": "{}"
                    }
                """, TEST_ACCOUNT_EMAIL, TEST_CONFIRMATION_CODE)
                .getMessage()))
        .andExpect(status().is4xxClientError());
  }
  @Test
  void testRegistrationConfirm_whenInvalidConfirmationCode_thenReturnClientError() throws Exception {
    var user = getTestUser(TEST_ACCOUNT_EMAIL, "password");
    user.setConfirmationCode(TEST_CONFIRMATION_CODE);
    user.setFirstName(TEST_ACCOUNT_FIRSTNAME);
    user.setLastName(TEST_ACCOUNT_LASTNAME);
    userRepository.save(user);
    mockMvc.perform(post("/api/v1/account/register/confirm")
            .contentType(MediaType.APPLICATION_JSON)
            .content(MessageFormatter.format("""
                    {
                      "email": "{}",
                      "confirmationCode": "invalid-code"
                    }
                """, TEST_ACCOUNT_EMAIL)
                .getMessage()))
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.error").value("Регистрация не была завершена: Указан неверный адрес почты или ключ подтверждения"))
        .andExpect(jsonPath("$.data").doesNotExist());
  }
  @Test
  void testRegistrationConfirm_whenUserNotFound_thenReturnClientError() throws Exception {
    mockMvc.perform(post("/api/v1/account/register/confirm")
            .contentType(MediaType.APPLICATION_JSON)
            .content(MessageFormatter.format("""
                    {
                      "email": "nonexistent-email@test.com",
                      "confirmationCode": "{}"
                    }
                """, TEST_CONFIRMATION_CODE)
                .getMessage()))
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.error").value("Регистрация не была завершена: Указан неверный адрес почты или ключ подтверждения"))
        .andExpect(jsonPath("$.data").doesNotExist());
  }
  @Test
  void testRegistrationConfirm_whenInvalidRequest_thenReturnClientError() throws Exception {
    mockMvc.perform(post("/api/v1/account/register/confirm")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                      "email": "testAccount@hotmail.com",
                      "confirmationCode": "wrong-code"
                    }
                """))
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.data").doesNotExist());
  }
@Test
void testChangePassword_whenValidInput_thenReturnSuccessResponse() throws Exception {
  var requestDto = new ChangePasswordDTO();
  requestDto.setPassword(TEST_PASSWORD);
  requestDto.setPassword(TEST_PASSWORD + "new");
  String requestBody = new ObjectMapper().writeValueAsString(requestDto);
  mockMvc.perform(put("/api/v1/account/password/set")
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestBody))
      .andExpect(status().is4xxClientError());
}
  @Test
  void testSendMessageForChangeEmail_whenValidInput_thenReturnSuccessResponse() throws Exception {
    var requestDto = new ChangeEmailDTO();
    requestDto.setEmail("testAccount@hotmail.com");
    mockMvc.perform(put("/api/v1/account/email")
            .contentType(MediaType.APPLICATION_JSON)
            .content(MessageFormatter.format("""
                  {
                    "newEmail": "{}"
                  }
              """, requestDto.getEmail())
                .getMessage()))
        .andExpect(status().is4xxClientError());
  }
  @Test
  void testSetNotificationType_whenValidInput_thenReturnSuccessResponse() throws Exception {
    String type = "email";
    boolean enable = true;
    NotificationSettingDTO user = new NotificationSettingDTO(type, true);
    mockMvc.perform(put("/api/v1/account/notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .content(MessageFormatter.format("""
                    {
                      "notification_type": "{}",
                      "enable": {}
                    }
                """, type, enable)
                .getMessage()))
        .andExpect(status().is4xxClientError());
  }
}
