package ru.skillbox.zerone_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.skillbox.zerone_backend.model.enumerated.FriendshipCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "friendship_status")
@Data
@Builder
public class FriendshipStatus {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private long id;

  @NotNull
  @Column(name = "time", columnDefinition = "timestamp without time zone")
  private LocalDateTime time;

  @NotNull
  @Column(name = "name")
  private String name;

  @NotNull
  @Column(name = "code", columnDefinition = "friendship_code")
  @Enumerated(EnumType.STRING)
  private FriendshipCode code;

  @OneToMany(mappedBy = "status", fetch = FetchType.LAZY)
  private List<Friendship> friendships = new ArrayList<>();
}
