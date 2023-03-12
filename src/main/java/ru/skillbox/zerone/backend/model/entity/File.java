package ru.skillbox.zerone.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Accessors(chain = true)
@Data
@Table(name = "file")
public class File {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "public_id")
  private String publicId;
  @Column(name = "url")
  private String url;
  @Column(name = "format")
  private String format;
}