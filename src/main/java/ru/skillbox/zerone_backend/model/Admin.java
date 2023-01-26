package ru.skillbox.zerone_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.skillbox.zerone_backend.enumerated.UserType;

@Entity
@Data
public class Admin {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String name;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "user_type")
  private UserType type;
}
