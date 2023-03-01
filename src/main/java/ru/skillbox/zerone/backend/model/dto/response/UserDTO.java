package ru.skillbox.zerone.backend.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.skillbox.zerone.backend.model.enumerated.MessagePermissions;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
  private Long id;
  @JsonProperty("first_name")
  private String firstName;
  @JsonProperty("last_name")
  private String lastName;
  private String email;
  private String country;
  private String city;
  @JsonProperty("birth_date")
  private LocalDate birthDate;
  @JsonProperty("reg_date")
  private LocalDateTime regDate;
  private String photo;
  private String about;
  private String token;
  @JsonProperty("is_blocked")
  private boolean isBlocked;
  @JsonProperty("is_deleted")
  private boolean isDeleted;
  @JsonProperty("messages_permission")
  private MessagePermissions messagePermissions;
  @JsonProperty("last_online_time")
  private LocalDateTime lastOnlineTime;
  private String phone;
}