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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class PostControllerTest extends AbstractIntegrationTest {

  private static final String TEST_POST_TITLE = "TestTitle";
  private static final String TEST_POST_TEXT = "Lorem Ipsum";
  private static final String WRONG_USER_ALERT = "Попытка публикации неизвестным пользователем";
  private static final String INVALID_POST_ALERT = "Пост с указанным id не найден";
  private static final String INVALID_USER_ALERT = "Попытка редактирования неизвестным пользователем";
  private static final String EDITED_TEST_POST_TITLE = "Новое название";
  private static final String EDITED_TEST_POST_TEXT = "Новый какой-то текст";

  @Autowired
  private MockMvc mockMvc;


  @Test
  @Sql(scripts = {"classpath:mock-my-tags-insert.sql", "classpath:truncate-all-users-cascade.sql", "classpath:mock-current-user-insert.sql"})
  @WithUserDetails("testAccount@hotmail.com")
  void testPostPublication_whenValidInput_thenReturnSuccessResponse() throws Exception {
    mockMvc.perform(post("/api/v1/users/1/wall")
            .contentType(MediaType.APPLICATION_JSON)
            .content(MessageFormatter.format("""
                        {
                          "title": "{}",
                          "post_text": "{}",
                          "tags": [ "sport", "music" ]
                        }
                    """, TEST_POST_TITLE, TEST_POST_TEXT)
                .getMessage()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.title").value(TEST_POST_TITLE))
        .andExpect(jsonPath("$.data.post_text").value(TEST_POST_TEXT));
  }

  @Test
  @Sql(scripts = {"classpath:truncate-all-users-cascade.sql", "classpath:mock-current-user-insert.sql"})
  @WithUserDetails("testAccount@hotmail.com")
  void testPostPublication_whenInputWrongUser_thenThrowsException() throws Exception {
    mockMvc.perform(post("/api/v1/users/2/wall")
            .contentType(MediaType.APPLICATION_JSON)
            .content(MessageFormatter.format("""
                        {
                          "title": "{}",
                          "post_text": "{}",
                          "tags": [ "sport", "music" ]
                        }
                    """, TEST_POST_TITLE, TEST_POST_TEXT)
                .getMessage()))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.error").value(WRONG_USER_ALERT));
  }

  @Test
  @Sql(scripts = {"classpath:truncate-all-users-cascade.sql", "classpath:mock-current-user-insert.sql",
      "classpath:mock-posts-insert.sql"})
  @WithUserDetails("testAccount@hotmail.com")
  void testAuthorWall_whenValidInput_thenReturnPackOfMyPosts() throws Exception {
    mockMvc.perform(get("/api/v1/users/1/wall"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.total").value(4));
  }

  @Test
  @Sql(scripts = {"classpath:truncate-all-users-cascade.sql", "classpath:mock-current-user-insert.sql",
      "classpath:mock-posts-insert.sql"})
  @WithUserDetails("testAccount@hotmail.com")
  void testFeeds_whenValidInput_thenReturnPackOfNotMyPosts() throws Exception {
    mockMvc.perform(get("/api/v1/feeds"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.total").value(1));
  }

  @Test
  @Sql(scripts = {"classpath:truncate-all-users-cascade.sql", "classpath:mock-current-user-insert.sql",
      "classpath:mock-posts-insert.sql"})
  @WithUserDetails("testAccount@hotmail.com")
  void testPostId_whenValidInput_thenReturnPostById() throws Exception {
    mockMvc.perform(get("/api/v1/post/1"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.title").value("Первый пост"));
  }

  @Test
  @Sql(scripts = {"classpath:truncate-all-users-cascade.sql", "classpath:mock-current-user-insert.sql",
      "classpath:mock-posts-insert.sql"})
  @WithUserDetails("testAccount@hotmail.com")
  void testPostId_whenInvalidInput_thenThrowsException() throws Exception {
    mockMvc.perform(get("/api/v1/post/7"))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.error").value(INVALID_POST_ALERT));
  }

  @Test
  @Sql(scripts = {"classpath:truncate-all-users-cascade.sql", "classpath:mock-current-user-insert.sql",
      "classpath:mock-posts-insert.sql"})
  @WithUserDetails("testAccount@hotmail.com")
  void testDeletePost_whenValidInput_thenDeletePostById() throws Exception {
    mockMvc.perform(delete("/api/v1/post/1"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.type").value("DELETED"));
  }

  @Test
  @Sql(scripts = {"classpath:truncate-all-users-cascade.sql", "classpath:mock-current-user-insert.sql",
      "classpath:mock-posts-insert.sql"})
  @WithUserDetails("testAccount@hotmail.com")
  void testDeletePost_whenInvalidPost_thenThrowsException() throws Exception {
    mockMvc.perform(delete("/api/v1/post/7"))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.error").value(INVALID_POST_ALERT));
  }

  @Test
  @Sql(scripts = {"classpath:truncate-all-users-cascade.sql", "classpath:mock-current-user-insert.sql",
      "classpath:mock-posts-insert.sql"})
  @WithUserDetails("testAccount@hotmail.com")
  void testDeletePost_whenInvalidUser_thenThrowsException() throws Exception {
    mockMvc.perform(delete("/api/v1/post/4"))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.error").value(INVALID_USER_ALERT));
  }


  @Test
  @Sql(scripts = {"classpath:truncate-all-users-cascade.sql", "classpath:mock-current-user-insert.sql",
      "classpath:mock-posts-insert.sql"})
  @WithUserDetails("testAccount@hotmail.com")
  void testRecoverPost_whenValidInput_thenReturnsRecoveredPost() throws Exception {
    mockMvc.perform(put("/api/v1/post/5/recover"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.type").value("POSTED"));
  }

  @Test
  @Sql(scripts = {"classpath:truncate-all-users-cascade.sql", "classpath:mock-current-user-insert.sql",
      "classpath:mock-posts-insert.sql"})
  @WithUserDetails("testAccount@hotmail.com")
  void testRecoverPost_whenInvalidPost_thenThrowsException() throws Exception {
    mockMvc.perform(put("/api/v1/post/7/recover"))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.error").value(INVALID_POST_ALERT));
  }

  @Test
  @Sql(scripts = {"classpath:truncate-all-users-cascade.sql", "classpath:mock-current-user-insert.sql",
      "classpath:mock-posts-insert.sql"})
  @WithUserDetails("testAccount@hotmail.com")
  void testRecoverPost_whenInvalidUser_thenThrowsException() throws Exception {
    mockMvc.perform(put("/api/v1/post/6/recover"))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.error").value(INVALID_USER_ALERT));
  }

  @Test
  @Sql(scripts = {"classpath:mock-my-tags-insert.sql", "classpath:truncate-all-users-cascade.sql", "classpath:mock-current-user-insert.sql",
      "classpath:mock-posts-insert.sql"})
  @WithUserDetails("testAccount@hotmail.com")
  void testPostEdition_whenValidInput_thenReturnsEditedPost() throws Exception {
    mockMvc.perform(put("/api/v1/post/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(MessageFormatter.format("""
                        {
                          "title": "{}",
                          "post_text": "{}",
                          "tags": [ "sport", "music" ]
                        }
                    """, EDITED_TEST_POST_TITLE, EDITED_TEST_POST_TEXT)
                .getMessage()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.title").value("Новое название"))
        .andExpect(jsonPath("$.data.post_text").value("Новый какой-то текст"));
  }

  @Test
  @Sql(scripts = {"classpath:truncate-all-users-cascade.sql", "classpath:mock-current-user-insert.sql",
      "classpath:mock-posts-insert.sql"})
  @WithUserDetails("testAccount@hotmail.com")
  void testPostEdition_whenInvalidPost_thenThrowsException() throws Exception {
    mockMvc.perform(put("/api/v1/post/7")
            .contentType(MediaType.APPLICATION_JSON)
            .content(MessageFormatter.format("""
                        {
                          "title": "{}",
                          "post_text": "{}",
                          "tags": [ "sport", "music" ]
                        }
                    """, EDITED_TEST_POST_TITLE, EDITED_TEST_POST_TEXT)
                .getMessage()))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.error").value(INVALID_POST_ALERT));
  }

  @Test
  @Sql(scripts = {"classpath:truncate-all-users-cascade.sql", "classpath:mock-current-user-insert.sql",
      "classpath:mock-posts-insert.sql"})
  @WithUserDetails("testAccount@hotmail.com")
  void testPostEdition_whenInvalidUser_thenThrowsException() throws Exception {
    mockMvc.perform(put("/api/v1/post/4")
            .contentType(MediaType.APPLICATION_JSON)
            .content(MessageFormatter.format("""
                        {
                          "title": "{}",
                          "post_text": "{}",
                          "tags": [ "sport", "music" ]
                        }
                    """, EDITED_TEST_POST_TITLE, EDITED_TEST_POST_TEXT)
                .getMessage()))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.error").value(INVALID_USER_ALERT));
  }
}
