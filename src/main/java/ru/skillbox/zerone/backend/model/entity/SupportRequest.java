package ru.skillbox.zerone.backend.model.entity;

import jakarta.persistence.*;
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
  @Column(name = "first_name")
  private String firstName;

  @NotNull
  @Column(name = "last_name")
  private String lastName;

  @NotNull
  @Column(name = "email")
  private String email;

  @NotNull
  @Column(name = "time", columnDefinition = "timestamp without time zone")
  private LocalDateTime time;

  @NotNull
  @Column(name = "status", columnDefinition = "support_request_status")
  @Enumerated(EnumType.STRING)
  private SupportRequestStatus status;
}
