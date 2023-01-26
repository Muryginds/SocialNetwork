package ru.skillbox.zerone_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Comment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @NotNull
  @Column(columnDefinition = "timestamp without time zone")
  private LocalDateTime time;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false,
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "comment_post_fk")
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Post post;

  @OneToMany(
      mappedBy = "comment",
      fetch = FetchType.LAZY)
  private Set<BlockHistory> blockHistories = new HashSet<>();

  @OneToMany(
      mappedBy = "parent",
      fetch = FetchType.LAZY)
  private Set<Comment> parents = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id",
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "comment_parent_comment_fk")
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Comment parent;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false,
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "comment_author_fk")
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User author;

  @NotNull
  @Column(columnDefinition = "text")
  private String commentText;

  @Column(nullable = false)
  private boolean isBlocked;

  @Column(nullable = false)
  private boolean isDeleted;
}
