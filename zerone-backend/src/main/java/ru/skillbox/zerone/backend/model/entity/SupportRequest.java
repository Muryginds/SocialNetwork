package ru.skillbox.zerone.backend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.zerone.backend.model.enumerated.SupportRequestStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "support_request")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupportRequest {
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
  @NotBlank
  @Column(name = "email")
  private String email;

  @NotNull
  @NotBlank
  @Column(name = "message")
  private String message;

  @NotNull
  @Builder.Default
  @Column(name = "time")
  private LocalDateTime time = LocalDateTime.now();

  @NotNull
  @Builder.Default
  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private SupportRequestStatus status = SupportRequestStatus.NEW;

  @Column(name = "answer")
  private String answer;
}
