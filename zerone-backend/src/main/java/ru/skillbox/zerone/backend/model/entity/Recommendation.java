package ru.skillbox.zerone.backend.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "recommendation")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Recommendation {

  @Id
  @Column(name = "id")
  private Long id;

  @Column(name = "recommended_friends")
  private List<Long> recommendedFriends;
}
