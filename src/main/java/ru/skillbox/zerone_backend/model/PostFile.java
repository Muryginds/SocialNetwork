package ru.skillbox.zerone_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_file",
    indexes = @Index(name = "post_file_post_id_idx", columnList = "post_id")
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
