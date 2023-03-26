package ru.skillbox.zerone.backend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import ru.skillbox.zerone.backend.model.enumerated.FriendshipStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "friendship")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Friendship {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull
  @Column(name = "status", columnDefinition = "friendship_status")
  @Enumerated(EnumType.STRING)
  private FriendshipStatus status;

  @NotNull
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "src_person_id", referencedColumnName = "id")
  private User srcPerson;

  @NotNull
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "dst_person_id", referencedColumnName = "id")
  private User dstPerson;

  @NotNull
  @Builder.Default
  @UpdateTimestamp
  @Column(name = "time", columnDefinition = "timestamp without time zone")
  private LocalDateTime time = LocalDateTime.now();
}
