package ru.skillbox.zerone.backend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "recommendation",
    indexes = {
        @Index(name = "recommendation_user_id_idx", columnList = "user_id")
    }
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Recommendation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "recommendation_user_fk"))
  User user;
  @Column(name = "recommended_friends")
  List<Long> recommendedFriends;
}
