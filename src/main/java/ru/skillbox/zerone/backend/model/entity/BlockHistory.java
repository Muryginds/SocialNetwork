package ru.skillbox.zerone.backend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.skillbox.zerone.backend.model.enumerated.ActionType;

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
  private Long id;

  @NotNull
  @Builder.Default
  @Column(name = "time", columnDefinition = "timestamp without time zone")
  private LocalDateTime time = LocalDateTime.now();

  @NotNull
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "block_history_user_fk")
  )
  private User user;

  @NotNull
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "post_id", referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "block_history_post_fk")
  )
  private Post post;

  @NotNull
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "comment_id", referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "block_history_comment_fk")
  )
  private Comment comment;

  @NotNull
  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(name = "action", columnDefinition = "action_type default 'BLOCK'")
  private ActionType action = ActionType.BLOCK;
}
