package ru.skillbox.zerone.backend.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data

public class CommonListDTO<T> {
  private String error;
  private int total;
  private int offset;
  private int perPage;
  private List<T> data;
  private LocalDateTime timestamp = LocalDateTime.now();


}
