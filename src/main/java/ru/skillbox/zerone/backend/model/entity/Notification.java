package ru.skillbox.zerone.backend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.skillbox.zerone.backend.model.enumerated.NotificationType;
import ru.skillbox.zerone.backend.model.enumerated.ReadStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification",
    indexes = {
        @Index(name = "notification_person_id_idx", columnList = "person_id"),
        @Index(name = "notification_comment_id_idx", columnList = "comment_id"),
        @Index(name = "notification_friendship_id_idx", columnList = "friendship_id"),
        @Index(name = "notification_post_id_idx", columnList = "post_id"),
        @Index(name = "notification_message_id_idx", columnList = "message_id"),
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
  @Column(name = "type_id", columnDefinition = "notification_type")
  private NotificationType typeId;

  @NotNull
  @Builder.Default
  @Column(name = "sent_time", columnDefinition = "timestamp without time zone")
  private LocalDateTime sentTime = LocalDateTime.now();

  @NotNull
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "person_id", referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "notification_person_fk")
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User person;

  @NotNull
  @Column(name = "entity_id")
  private Long entityId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "comment_id", referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "notification_comment_fk")
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Comment comment;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "friendship_id", referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "notification_friendship_fk")
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Friendship friendship;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "post_id", referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "notification_post_fk")
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Post post;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "message_id", referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "notification_message_fk")
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Message message;

  @NotNull
  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(name = "status", columnDefinition = "read_status default 'SENT'")
  private ReadStatus status = ReadStatus.SENT;
}
