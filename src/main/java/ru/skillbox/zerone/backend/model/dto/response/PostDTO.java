package ru.skillbox.zerone.backend.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.skillbox.zerone.backend.model.enumerated.PostType;


import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDTO {
  private long id;
  private String title;
  @JsonProperty("post_text")
  private String postText;
  @JsonProperty("is_blocked")
  private boolean isBlocked;
  private int likes;
  private CommonListResponseDTO<CommentDTO> comments;
  @JsonProperty("my_like")
  private boolean myLike;
  private List<String> tags;
  private PostType type;
  private UserDTO author;
  private LocalDateTime timestamp = LocalDateTime.now();
}
