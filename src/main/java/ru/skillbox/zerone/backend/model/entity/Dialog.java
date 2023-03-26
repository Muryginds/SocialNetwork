package ru.skillbox.zerone.backend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dialog")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Dialog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "sender_id", referencedColumnName = "id")
  private User sender;

  @NotNull
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "recipient_id", referencedColumnName = "id")
  private User recipient;
}
