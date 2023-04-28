package ru.skillbox.zerone.backend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;
import ru.skillbox.zerone.backend.model.enumerated.LikeType;

@Data
public class LikeRequestDTO {
  @JsonProperty("item_id")
  private Long id;
  private LikeType type;

  @JsonSetter("type")
  public void setType(String type) {
    this.type = switch (type) {
      case "Post" -> LikeType.POST;
      case "Comment" -> LikeType.COMMENT;
      default -> throw new IllegalArgumentException();
    };
  }
}
