package ru.skillbox.zerone.backend.model.dto.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TagDTO {

  private Long id;

  private String tag;
}
