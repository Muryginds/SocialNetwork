package ru.skillbox.zerone_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_to_tag",
    indexes = {
        @Index(name = "post_to_tag_post_id_idx", columnList = "post_id"),
        @Index(name = "post_to_tag_tag_id_idx", columnList = "tag_id")
    }
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostToTag {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false,
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "post_to_tag_post_fk")
  )
  private Post post;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tag_id", nullable = false,
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "post_to_tag_tag_fk")
  )
  private Tag tag;
}
