package ru.skillbox.zerone.backend.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;

@Data
public class WriteCommentDTO {

  @JsonProperty("comment_text")
  private String commentText;
  private HashMap<String ,String > images; //ID и URL

  @JsonProperty("parent_id")
  private Integer parentId;

}
