package ru.skillbox.zerone_backend.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "post_to_tag")
@Data
@Builder
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
