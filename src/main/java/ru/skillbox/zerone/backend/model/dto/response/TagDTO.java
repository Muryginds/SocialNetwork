package ru.skillbox.zerone.backend.model.dto.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TagDTO {

  private int id;

  private String tag;
}
