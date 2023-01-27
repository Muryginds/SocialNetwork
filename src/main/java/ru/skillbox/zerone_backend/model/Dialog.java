package ru.skillbox.zerone_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dialog",
    indexes = {
        @Index(name = "dialog_sender_id_idx", columnList = "sender_id"),
        @Index(name = "dialog_recipient_id_idx", columnList = "recipient_id")
    }
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Dialog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private long id;

  @OneToMany(mappedBy = "dialog", fetch = FetchType.LAZY)
  private List<Message> messages = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sender_id", nullable = false,
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "dialog_sender_user_fk")
  )
  private User sender;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recipient_id", nullable = false,
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "dialog_recipient_user_fk")
  )
  private User recipient;
}
