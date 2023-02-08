package ru.skillbox.zerone.backend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.zerone.backend.model.enumerated.FriendshipCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "friendship_status")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendshipStatus {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull
  @Builder.Default
  @Column(name = "time", columnDefinition = "timestamp without time zone")
  private LocalDateTime time = LocalDateTime.now();

  @NotNull
  @Column(name = "name")
  private String name;

  @NotNull
  @Builder.Default
  @Column(name = "code", columnDefinition = "friendship_code default 'REQUEST'")
  @Enumerated(EnumType.STRING)
  private FriendshipCode code = FriendshipCode.REQUEST;
}
