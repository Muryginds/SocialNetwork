package ru.skillbox.zerone.backend.model.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.skillbox.zerone.backend.model.enumerated.LikeType;

import java.time.LocalDateTime;

@Entity
@Table(name = "`like`", indexes = {
    @Index(name = "like_post_id_idx", columnList = "post_id"),
    @Index(name = "like_comment_id_idx", columnList = "comment_id"),
    @Index(name = "like_user_id_idx", columnList = "user_id")
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Like {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private long id;

  @NotNull
  @Builder.Default
  @Column(name = "time", columnDefinition = "timestamp without time zone")
  private LocalDateTime time = LocalDateTime.now();

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "post_id", referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "like_post_fk")
  )
  private Post post;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "comment_id", referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "like_comment_fk")
  )
  private Comment comment;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "type", columnDefinition = "like_type")
  private LikeType type;

  @NotNull
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "like_user_fk")
  )
  private User user;
}
