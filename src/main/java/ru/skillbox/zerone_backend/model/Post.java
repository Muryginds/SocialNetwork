package ru.skillbox.zerone_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
@Data
public class Post {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private long id;

  @NotNull
  @Column(name = "time", columnDefinition = "timestamp without time zone")
  private LocalDateTime time;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false,
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "post_author_fk")
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User author;

  @NotNull
  @Column(name = "title")
  private String title;

  @NotNull
  @Column(name = "post_text", columnDefinition = "text")
  private String postText;

  @NotNull
  @Column(name = "update_date", columnDefinition = "timestamp without time zone")
  private LocalDateTime updateDate;

  @NotNull
  @Column(name = "is_blocked")
  private boolean isBlocked;

  @NotNull
  @Column(name = "is_deleted")
  private boolean isDeleted;

  @OneToMany(
      mappedBy = "post",
      fetch = FetchType.LAZY)
  private List<BlockHistory> blockHistories = new ArrayList<>();

  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
  private List<Comment> comments = new ArrayList<>();
}
