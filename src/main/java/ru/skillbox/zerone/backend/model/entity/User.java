package ru.skillbox.zerone.backend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.skillbox.zerone.backend.model.enumerated.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "`user`")
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull
  @NotBlank
  @Column(name = "first_name")
  private String firstName;

  @NotNull
  @NotBlank
  @Column(name = "last_name")
  private String lastName;

  @NotNull
  @Builder.Default
  @Column(name = "reg_date", columnDefinition = "timestamp without time zone")
  private LocalDateTime regDate = LocalDateTime.now();

  @Column(name = "birth_date")
  private LocalDate birthDate;

  @NotNull
  @NotBlank
  @Column(name = "email")
  private String email;

  @Column(name = "phone")
  private String phone;

  @NotNull
  @NotBlank
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
  @NotBlank
  @Column(name = "confirmation_code")
  private String confirmationCode;

  @NotNull
  @Builder.Default
  @Column(name = "is_approved", columnDefinition = "boolean default false")
  private Boolean isApproved = false;

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

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "user_to_role",
      joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
  private List<Role> roles;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return !isBlocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return isApproved;
  }
}
