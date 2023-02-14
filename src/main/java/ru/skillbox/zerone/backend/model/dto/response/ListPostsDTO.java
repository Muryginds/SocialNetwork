package ru.skillbox.zerone.backend.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data

public class ListPostsDTO<T> {
  private String error;
  private int total;
  private int offset;
  private int perPage;
  private ListPostsDTO <PostsDTO> data;
  private LocalDateTime timestamp = LocalDateTime.now();


}
