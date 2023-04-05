package ru.skillbox.zerone.backend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "change_email_history")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangeEmailHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull
  @NotBlank
  @Column(name = "email_new")
  private String emailNew;

  @NotNull
  @NotBlank
  @Column(name = "email_old")
  private String emailOld;

  @NotNull
  @Builder.Default
  @Column(name = "time")
  private LocalDateTime time = LocalDateTime.now();
}
