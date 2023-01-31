package ru.skillbox.zerone.backend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comment",
    indexes = {
        @Index(name = "comment_post_id_idx", columnList = "post_id"),
        @Index(name = "comment_parent_id_idx", columnList = "parent_id"),
        @Index(name = "comment_author_id_idx", columnList = "author_id")
    }
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
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
  @JoinColumn(name = "post_id", referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "comment_post_fk")
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Post post;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "parent_id", referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "comment_parent_comment_fk")
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Comment parent;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "author_id", nullable = false, referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "comment_author_fk")
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User author;

  @NotNull
  @Column(name = "comment_test", columnDefinition = "text")
  private String commentText;

  @Builder.Default
  @Column(name = "is_blocked", columnDefinition = "boolean default false")
  private Boolean isBlocked = false;

  @Builder.Default
  @Column(name = "is_deleted", columnDefinition = "boolean default false")
  private Boolean isDeleted = false;
}
