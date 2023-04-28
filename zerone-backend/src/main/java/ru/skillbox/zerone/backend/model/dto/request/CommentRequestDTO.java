package ru.skillbox.zerone.backend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.zerone.backend.model.dto.response.StorageDTO;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentRequestDTO {
  @JsonProperty("parent_id")
  private Long parentId;
  @JsonProperty("comment_text")
  private String commentText;
  @JsonProperty("images")
  private List<StorageDTO> images;
}
