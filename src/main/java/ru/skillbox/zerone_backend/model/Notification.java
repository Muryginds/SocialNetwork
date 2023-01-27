package ru.skillbox.zerone_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.skillbox.zerone_backend.model.enumerated.ReadStatus;
import ru.skillbox.zerone_backend.model.enumerated.UserStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data
@Builder
public class Notification {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "type_id", nullable = false,
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "notification_notification_type_fk")
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  private NotificationType typeId;

  @NotNull
  @Column(name = "sent_time", columnDefinition = "timestamp without time zone")
  private LocalDateTime sentTime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "person_id", nullable = false,
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "notification_person_fk")
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User person;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "entity_id", nullable = false,
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "notification_entity_type_fk")
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  private EntityType entity;

  @NotNull
  @Column(name = "status", columnDefinition = "read_status")
  private ReadStatus status;
}
