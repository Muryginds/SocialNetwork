package ru.skillbox.zerone.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.skillbox.zerone.backend.model.enumerated.MessagePermissions;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
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
  private boolean deleted;

  @JsonProperty("message_permissions")
  private MessagePermissions messagesPermission;

  @JsonProperty("last_online_time")
  private LocalDateTime lastOnlineTime;

  private String phone;
}