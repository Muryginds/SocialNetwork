package ru.skillbox.zerone.admin.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TotalCommentDto {
  private long total;
  @JsonProperty("per_page")
  private int perPage;
  private int offset;
  @JsonProperty("comment_list")
  private List<CommentDto> commentList;
}
