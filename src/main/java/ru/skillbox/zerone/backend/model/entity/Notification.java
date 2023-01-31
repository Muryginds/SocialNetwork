package ru.skillbox.zerone.backend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.skillbox.zerone.backend.model.enumerated.ReadStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification",
    indexes = {
        @Index(name = "notification_type_id_idx", columnList = "type_id"),
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
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "type_id", referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "notification_notification_type_fk")
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  private NotificationType typeId;

  @NotNull
  @Column(name = "sent_time", columnDefinition = "timestamp without time zone")
  private LocalDateTime sentTime;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "person_id", referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "notification_person_fk")
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User person;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "entity_id", referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "notification_entity_type_fk")
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  private EntityType entity;

  @NotNull
  @Column(name = "status", columnDefinition = "read_status")
  private ReadStatus status;
}
