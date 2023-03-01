package ru.skillbox.zerone.backend.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.skillbox.zerone.backend.model.dto.response.UserDTO;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class CommentDTO {
  @JsonProperty("parent_id")
  private Integer parentId;
  @JsonProperty("comment_text")
  private String commentText;
  private int id;
  @JsonProperty("post_id")
  private int postId;
  private LocalDateTime time;
  private UserDTO author;
  @JsonProperty("is_blocked")
  private boolean isBlocked;
  @JsonProperty("is_deleted")
  private boolean isDeleted;
  @JsonProperty("sub_comments")
  private List<CommentDTO> subComments;
  private int likes;
  @JsonProperty("my_like")
  private boolean myLike;
  @JsonProperty("images")
  private List<ImageDTO> images;
}
