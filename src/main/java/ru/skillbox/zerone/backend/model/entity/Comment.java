package ru.skillbox.zerone.backend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import ru.skillbox.zerone.backend.model.enumerated.CommentType;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
  private int id;

  @NotNull
  @Builder.Default
  @Column(name = "time", columnDefinition = "timestamp without time zone")
  private LocalDateTime time = LocalDateTime.now();

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "type", columnDefinition = "comment_type")
  private CommentType type;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "post_id", referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "comment_post_fk")
  )
  private Post post;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "parent_id", referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "comment_parent_comment_fk")
  )
  private Comment parent;


  @NotNull
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "author_id", nullable = false, referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "comment_author_fk")
  )
  private User author;

  @NotNull
  @NotBlank
  @Column(name = "comment_text", columnDefinition = "text")
  private String commentText;

  @NotNull
  @Builder.Default
  @Column(name = "is_blocked", columnDefinition = "boolean default false")
  private static Boolean isBlocked = false;

  @NotNull
  @Builder.Default
  @Column(name = "is_deleted", columnDefinition = "boolean default false")
  private Boolean isDeleted = false;

}
