package ru.skillbox.zerone.backend.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.skillbox.zerone.backend.model.enumerated.CommentType;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentDTO {
  @JsonProperty("parent_id")
  private Long parentId;
  @JsonProperty("comment_text")
  private String commentText;
  private long id;
  @JsonProperty("post_id")
  private long post;
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
  private List<StorageDTO> images;
  @JsonProperty("comment_type")
  private CommentType type;
}
