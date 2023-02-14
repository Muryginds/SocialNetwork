package ru.skillbox.zerone.backend.model.dto.request;

import lombok.Data;

import java.util.List;
@Data
public class PostRequestDTO {
  private String title;
  private String postText;
  private List<String> tags;

}
