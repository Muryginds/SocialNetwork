package ru.skillbox.zerone.backend.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "country")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Country {
  @Id
  @Column(name = "id")
  private Short id;

  @NotNull
  @NotBlank
  @Column(name = "name")
  private String name;
}
