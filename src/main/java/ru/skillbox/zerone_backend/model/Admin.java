package ru.skillbox.zerone_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.skillbox.zerone_backend.model.enumerated.UserType;

@Entity
@Table(name = "admin")
@Data
@Builder
public class Admin {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private long id;

  @NotNull
  @Column(name = "name")
  private String name;

  @NotNull
  @Column(name = "email")
  private String email;

  @NotNull
  @Column(name = "password")
  private String password;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "type", columnDefinition = "user_type")
  private UserType type;
}
