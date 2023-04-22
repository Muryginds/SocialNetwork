package ru.skillbox.zerone.backend.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import ru.skillbox.zerone.backend.AbstractIntegrationTest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.skillbox.zerone.backend.service.FriendService.CANNOT_ADD_YOURSELF;

@Transactional
class FriendsControllerTest extends AbstractIntegrationTest {
  private static final String API_URL = "/api/v1/friends";
  @Autowired
  private MockMvc mockMvc;

  @Test
  @WithUserDetails("esperanza.padberg@yahoo.com")
  void addFriend_whenAllCorrect_thenIsOk() throws Exception {
    mockMvc.perform(post(API_URL + "/52"))
        .andExpect(status().isOk());
  }

  @Test
  @WithUserDetails("esperanza.padberg@yahoo.com")
  void addFriend_whenAddItself_thenException() throws Exception {
    mockMvc.perform(post(API_URL + "/3"))
        .andExpect(jsonPath("$.error").value(CANNOT_ADD_YOURSELF));
  }
}
