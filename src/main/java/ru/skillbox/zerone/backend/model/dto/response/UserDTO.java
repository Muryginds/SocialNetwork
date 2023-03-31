package ru.skillbox.zerone.backend.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Модель данных с полной информацией о пользователе")
public class UserDTO {
  @Schema(description = "id пользователя, генерируется базой данных", example = "1")
  private Long id;
  @JsonProperty("first_name")
  @Schema(description = "имя пользователя", example = "Борис")
  private String firstName;
  @JsonProperty("last_name")
  @Schema(description = "фамилия пользователя", example = "Богданов")
  private String lastName;

  @Schema(description = "email пользователя", example = "Dimatch86@mail.ru")
  private String email;

  @Schema(description = "страна проживания пользователя", example = "Россия")
  private String country;

  @Schema(description = "город пользователя", example = "Волгодонск")
  private String city;
  @JsonProperty("birth_date")
  @Schema(description = "дата рожденя", example = "1986-01-27")
  private LocalDate birthDate;
  @JsonProperty("reg_date")
  @Schema(description = "дата регистрации", example = "2022-01-23")
  private LocalDateTime regDate;

  @Schema(description = "ссылка на фото пользователя", example = "https://res.cloudinary.com/zeroneproject/image/upload/v1680170245/pa4sdbs9iydwyfvgchuz.jpg")
  private String photo;

  @Schema(description = "о себе")
  private String about;

  @Schema(description = "токен авторизации")
  private String token;
  @JsonProperty("is_blocked")
  private boolean isBlocked;
  @JsonProperty("is_deleted")
  private boolean isDeleted;
  @JsonProperty("is_blocked_by_me")
  private boolean isBlockedByMe;
  @JsonProperty("last_online_time")
  @Schema(description = "дата последнего посещения", example = "2023-03-30")
  private LocalDateTime lastOnlineTime;

  @Schema(description = "телефон", example = "+7(960)459-10-15")
  private String phone;
}