package ru.skillbox.zerone.admin.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.zerone.admin.model.enumerated.CommentType;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "comment")
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
  @Column(name = "time")
  private LocalDateTime time = LocalDateTime.now();

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "type")
  private CommentType type;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "post_id", referencedColumnName = "id")
  private Post post;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "parent_id", referencedColumnName = "id")
  private Comment parent;

  @NotNull
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "author_id", referencedColumnName = "id")
  private User author;

  @NotNull
  @NotBlank
  @Column(name = "comment_text")
  private String commentText;

  @NotNull
  @Builder.Default
  @Column(name = "is_blocked")
  private Boolean isBlocked = false;

  @NotNull
  @Builder.Default
  @Column(name = "is_deleted")
  private Boolean isDeleted = false;

  @OneToMany
  @JoinColumn(name = "parent_id")
  private Set<Comment> comments = new HashSet<>();
}
