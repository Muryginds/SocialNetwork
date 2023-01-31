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
@Table(name = "`like`", indexes = {
    @Index(name = "like_entity_id_idx", columnList = "entity_id"),
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

  @NotNull
  @Column(name = "entity_id")
  private long entityId;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(name = "type", columnDefinition = "like_type default 'POST'")
  private LikeType type = LikeType.POST;

  @NotNull
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "like_user_fk")
  )
  private User user;
}
