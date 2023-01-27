package ru.skillbox.zerone_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.skillbox.zerone_backend.model.enumerated.ActionType;

import java.time.LocalDateTime;

@Entity
@Table(name = "block_history",
    indexes = {
        @Index(name = "block_history_user_id_idx", columnList = "user_id"),
        @Index(name = "block_history_post_id_idx", columnList = "post_id"),
        @Index(name = "block_history_comment_id_idx", columnList = "comment_id")
    }
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlockHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private long id;

  @NotNull
  @Column(name = "time", columnDefinition = "timestamp without time zone")
  private LocalDateTime time;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false,
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "block_history_user_fk")
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false,
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "block_history_post_fk")
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Post post;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "comment_id", nullable = false,
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "block_history_comment_fk")
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Comment comment;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "action", columnDefinition = "action_type")
  private ActionType action;
}
