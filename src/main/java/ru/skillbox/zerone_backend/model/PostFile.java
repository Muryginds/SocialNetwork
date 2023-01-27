package ru.skillbox.zerone_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "post_file")
@Data
@Builder
public class PostFile {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false, referencedColumnName = "id",
      foreignKey = @ForeignKey(name = " post_file_post_fk")
  )
  private Post post;

  @NotNull
  @Column(name = "path")
  private String path;
}
