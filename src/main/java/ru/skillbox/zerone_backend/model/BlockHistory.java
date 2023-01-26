package ru.skillbox.zerone_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.skillbox.zerone_backend.enumerated.ActionType;

import java.time.LocalDateTime;

@Entity
@Data
public class BlockHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @NotNull
  @Column(columnDefinition = "timestamp without time zone")
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
  @Column(columnDefinition = "action_type")
  private ActionType action;
}
