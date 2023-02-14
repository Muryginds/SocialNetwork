package ru.skillbox.zerone.backend.model.dto.response;

import lombok.Data;
import ru.skillbox.zerone.backend.model.dto.UserDTO;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostsDTO {
  private String title;
  private String postText;
  private boolean isBlocked;
  private int likes;
  private ListCommentsDTO<CommentDTO> comments;
  private boolean myLike;
  private List<String> tags;
  private String type;
  private UserDTO author;
  private LocalDateTime timestamp = LocalDateTime.now();
}
