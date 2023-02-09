package ru.skillbox.zerone.backend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.zerone.backend.model.enumerated.AdminType;

@Entity
@Table(name = "admin")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Admin {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull
  @NotBlank
  @Column(name = "name")
  private String name;

  @NotNull
  @NotBlank
  @Column(name = "email")
  private String email;

  @NotNull
  @NotBlank
  @Column(name = "password")
  private String password;

  @NotNull
  @Builder.Default
  @Column(name = "type", columnDefinition = "admin_type default 'MODERATOR'")
  @Enumerated(EnumType.STRING)
  private AdminType type = AdminType.MODERATOR;
}
