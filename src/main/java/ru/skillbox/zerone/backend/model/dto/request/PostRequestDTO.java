package ru.skillbox.zerone.backend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
public class PostRequestDTO {
  private String title;
  @JsonProperty("post_text")
  private String postText;
  private List<String> tags;

}
