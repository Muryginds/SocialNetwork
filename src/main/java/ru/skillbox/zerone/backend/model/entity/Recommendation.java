package ru.skillbox.zerone.backend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @NotNull
  @OneToOne
  @MapsId
  private User user;
  @Column(name = "recommended_friends")
  private List<Long> recommendedFriends;
}
