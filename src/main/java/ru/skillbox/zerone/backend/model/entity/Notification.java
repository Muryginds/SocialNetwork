package ru.skillbox.zerone.backend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.zerone.backend.model.enumerated.NotificationType;
import ru.skillbox.zerone.backend.model.enumerated.ReadStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification",
    indexes = {
        @Index(name = "notification_person_id_idx", columnList = "person_id"),
        @Index(name = "notification_entity_id_idx", columnList = "entity_id")
    }
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "type", columnDefinition = "notification_type")
  private NotificationType type;

  @NotNull
  @Builder.Default
  @Column(name = "sent_time", columnDefinition = "timestamp without time zone")
  private LocalDateTime sentTime = LocalDateTime.now();

  @NotNull
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "person_id", referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "notification_person_fk")
  )
  private User person;

  @NotNull
  @Column(name = "entity_id")
  private Long entityId;

  @NotNull
  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(name = "status", columnDefinition = "read_status default 'SENT'")
  private ReadStatus status = ReadStatus.SENT;
}
