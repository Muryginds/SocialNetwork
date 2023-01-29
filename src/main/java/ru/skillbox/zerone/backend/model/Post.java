package ru.skillbox.zerone.backend.model;

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
@Table(name = "post",
    indexes = @Index(name = "post_author_id_idx", columnList = "author_id")
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull
  @Column(name = "time", columnDefinition = "timestamp without time zone")
  private LocalDateTime time;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", referencedColumnName = "id",
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
  private Boolean isBlocked;

  @NotNull
  @Column(name = "is_deleted")
  private Boolean isDeleted;

  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
  private List<BlockHistory> blockHistories = new ArrayList<>();

  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
  private List<Comment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
  private List<PostToTag> postToTags = new ArrayList<>();

  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
  private List<PostFile> postFiles = new ArrayList<>();

  public void addBlockHistory(BlockHistory blockHistory) {
    if (!blockHistories.contains(blockHistory)) {
      blockHistories.add(blockHistory);
      blockHistory.setPost(this);
    }
  }

  public void addComment(Comment comment) {
    if (!comments.contains(comment)) {
      comments.add(comment);
      comment.setPost(this);
    }
  }

  public void removeComment(Comment comment) {
    if (comments.contains(comment)) {
      comments.remove(comment);
      comment.setPost(null);
    }
  }

  public void addPostToTag(PostToTag postToTag) {
    if (!postToTags.contains(postToTag)) {
      postToTags.add(postToTag);
      postToTag.setPost(this);
    }
  }

  public void removePostToTag(PostToTag postToTag) {
    if (postToTags.contains(postToTag)) {
      postToTags.remove(postToTag);
      postToTag.setPost(null);
    }
  }

  public void addPostFile(PostFile postFile) {
    if (!postFiles.contains(postFile)) {
      postFiles.add(postFile);
      postFile.setPost(this);
    }
  }

  public void removePostFile(PostFile postFile) {
    if (postFiles.contains(postFile)) {
      postFiles.remove(postFile);
      postFile.setPost(null);
    }
  }
}
