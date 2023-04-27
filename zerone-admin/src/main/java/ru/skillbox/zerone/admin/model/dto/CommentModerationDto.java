package ru.skillbox.zerone.admin.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class CommentModerationDto {
  private long id;
  @JsonProperty("post_title")
  private String postTitle;
  @JsonProperty("author_fullname")
  private String authorFullname;
  @JsonProperty("comment_text")
  private String commentText;
  @JsonProperty("is_blocked")
  private boolean isBlocked;
  @JsonProperty("is_deleted")
  private boolean isDeleted;
}
