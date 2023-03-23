package ru.skillbox.zerone.backend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DialogRequestDTO {
  @JsonProperty("users_ids")
  private List<Long> usersIds;
}
