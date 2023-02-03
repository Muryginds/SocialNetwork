package ru.skillbox.zerone.backend.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.model.enumerated.MessagePermissions;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
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
  private boolean isDeleted;
  @JsonProperty("message_permissions")
  private MessagePermissions messagesPermission;
  @JsonProperty("last_online_time")
  private LocalDateTime lastOnlineTime;
  private String phone;

  public static UserDto fromUser(User user) {
    return UserDto.builder()
        .id(user.getId())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .country(user.getCountry())
        .city(user.getCity())
        .birthDate(user.getBirthDate())
        .regDate(user.getRegDate())
        .photo(user.getPhoto())
        .about(user.getAbout())
        .isBlocked(user.getIsBlocked())
        .isDeleted(user.getIsDelete())
        .messagesPermission(user.getMessagePermissions())
        .lastOnlineTime(user.getLastOnlineTime())
        .photo(user.getPhone())
        .build();
  }
}