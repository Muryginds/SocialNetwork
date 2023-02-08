package ru.skillbox.zerone.backend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.skillbox.zerone.backend.model.enumerated.MessagePermissions;
import ru.skillbox.zerone.backend.model.enumerated.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "`user`",
    uniqueConstraints = {
    @UniqueConstraint(name = "user_email_uk", columnNames = {"email"})}
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull
  @Column(name = "first_name", columnDefinition = "text")
  private String firstName;

  @NotNull
  @Column(name = "last_name", columnDefinition = "text")
  private String lastName;

  @NotNull
  @Builder.Default
  @Column(name = "reg_date", columnDefinition = "timestamp without time zone")
  private LocalDateTime regDate = LocalDateTime.now();

  @Column(name = "birth_date")
  private LocalDate birthDate;

  @NotNull
  @Column(name = "email")
  private String email;

  @Column(name = "phone")
  private String phone;

  @NotNull
  @Column(name = "password")
  private String password;

  @Column(name = "photo")
  private String photo;

  @Column(name = "about", columnDefinition = "text")
  private String about;

  @NotNull
  @Builder.Default
  @Column(name = "status", columnDefinition = "user_status default 'INACTIVE'")
  @Enumerated(EnumType.STRING)
  private UserStatus status = UserStatus.INACTIVE;

  @Column(name = "country")
  private String country;

  @Column(name = "city")
  private String city;

  @NotNull
  @Column(name = "confirmation_code")
  private String confirmationCode;

  @NotNull
  @Builder.Default
  @Column(name = "is_approved", columnDefinition = "boolean default false")
  private Boolean isApproved = false;

  @NotNull
  @Builder.Default
  @Column(name = "message_permissions", columnDefinition = "message_permissions default 'ALL'")
  @Enumerated(EnumType.STRING)
  private MessagePermissions messagePermissions = MessagePermissions.ALL;

  @NotNull
  @Builder.Default
  @Column(name = "last_online_time", columnDefinition = "timestamp without time zone")
  private LocalDateTime lastOnlineTime = LocalDateTime.now();

  @NotNull
  @Builder.Default
  @Column(name = "is_blocked", columnDefinition = "boolean default false")
  private Boolean isBlocked = false;

  @NotNull
  @Builder.Default
  @Column(name = "is_deleted", columnDefinition = "boolean default false")
  private Boolean isDeleted = false;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "user2role",
      joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
  private List<Role> roles;
}
