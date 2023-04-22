package ru.skillbox.zerone.admin.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.zerone.admin.model.enumerated.SupportRequestStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "support_request",
    indexes = {
        @Index(name = "support_request_status_idx", columnList = "status")
    }
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupportRequest {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "email")
  private String email;

  @Column(name = "message", columnDefinition = "text")
  private String message;

  @Column(name = "time", columnDefinition = "timestamp without time zone")
  private LocalDateTime time;

  @Column(name = "status", columnDefinition = "support_request_status default 'NEW'")
  @Enumerated(EnumType.STRING)
  private SupportRequestStatus status;

  @Column(name = "answer", columnDefinition = "text")
  private String answer;
}
