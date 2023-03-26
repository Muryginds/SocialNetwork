package ru.skillbox.zerone.backend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.zerone.backend.model.enumerated.LikeType;

import java.time.LocalDateTime;

@Entity
@Table(name = "`like`")
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
  @JoinColumn(name = "post_id", referencedColumnName = "id")
  private Post post;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "comment_id", referencedColumnName = "id")
  private Comment comment;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "type", columnDefinition = "like_type")
  private LikeType type;

  @NotNull
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;
}
