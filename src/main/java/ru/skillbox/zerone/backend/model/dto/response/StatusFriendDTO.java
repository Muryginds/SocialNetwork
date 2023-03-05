package ru.skillbox.zerone.backend.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.skillbox.zerone.backend.model.enumerated.FriendshipStatus;

@Data
@AllArgsConstructor
public class StatusFriendDTO {
  @JsonProperty("user_id")
  private Long userId;
  private FriendshipStatus status;
}
