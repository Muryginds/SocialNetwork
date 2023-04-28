package ru.skillbox.zerone.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.skillbox.zerone.backend.AbstractIntegrationTest;
import ru.skillbox.zerone.backend.model.dto.request.CommentRequestDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommentDTO;
import ru.skillbox.zerone.backend.model.dto.response.CommonResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
class TestCommentController extends AbstractIntegrationTest {


  static CommentRequestDTO request;
  static CommonResponseDTO<CommentDTO> response;
  static CommentDTO commentDTO;
  static UserDTO userDTO;
  @Autowired
  private MockMvc mvc;
  @Autowired
  private ObjectMapper mapper;

  @BeforeAll
  static void init() {
    request = new CommentRequestDTO();
    request.setCommentText("some text");
    request.setParentId(78L);
    commentDTO = new CommentDTO();
    commentDTO.setCommentText(request.getCommentText());
    commentDTO.setParentId(request.getParentId());
    commentDTO.setAuthor(userDTO);
    commentDTO.setBlocked(false);
    commentDTO.setId(1L);
    response = new CommonResponseDTO<>();
    response.setData(commentDTO);
  }

  @WithUserDetails("weldon.hand@hotmail.com")
  @Test
  void addCommentAPI() throws Exception {
    mvc.perform(MockMvcRequestBuilders
            .post("/api/v1/post/{id}/comments", 1)
            .content(mapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding(StandardCharsets.UTF_8)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("data.comment_text").value(request.getCommentText()))
        .andExpect(jsonPath("data.post_id").value(1L))
        .andExpect(jsonPath("data.parent_id").value(request.getParentId()))
        .andExpect(jsonPath("data.author.id").isNotEmpty())
        .andExpect(jsonPath("data.is_blocked").value(false))
        .andExpect(jsonPath("error").doesNotExist())
        .andDo(print());
  }
}
