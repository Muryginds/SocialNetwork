package ru.skillbox.zerone.backend.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.skillbox.zerone.backend.AbstractIntegrationTest;
import ru.skillbox.zerone.backend.security.JwtTokenProvider;
import ru.skillbox.zerone.backend.testData.UserMockUtils;
import ru.skillbox.zerone.backend.util.CurrentUserUtils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class AuthenticationControllerTest extends AbstractIntegrationTest implements UserMockUtils {
  private static final String TEST_ACCOUNT_EMAIL = "testAccount@hotmail.com";
  private static final String TEST_ACCOUNT_FIRSTNAME = "Серж";
  private static final String TEST_ACCOUNT_LASTNAME = "Богданов";
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Test
  @Sql(scripts = "classpath:mock-user-insert.sql")
  void testLogin_whenValidInput_thenReturnSuccessResponse() throws Exception {
    mockMvc.perform(post("/api/v1/auth/login")
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
  @Sql(scripts = "classpath:mock-user-insert.sql")
  void testLogin_whenWrongPassword_thenReturnClientError() throws Exception {
    mockMvc.perform(post("/api/v1/auth/login")
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
    mockMvc.perform(post("/api/v1/auth/login")
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
    mockMvc.perform(post("/api/v1/auth/login")
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

  @Test
  @WithUserDetails(TEST_ACCOUNT_EMAIL)
  @Sql(scripts = "classpath:mock-user-insert.sql")
  void testLogout_whenValidToken_thenReturnSuccessResponse() throws Exception {
    var user = CurrentUserUtils.getCurrentUser();
    var token = jwtTokenProvider.createToken(user.getEmail(), user.getRoles());
    mockMvc.perform(get("/api/v1/auth/logout").header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.message").value("Logged out"));
  }
}
