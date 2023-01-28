package ru.skillbox.zerone.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.skillbox.zerone.backend.model.enumerated.ReadStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "message",
    indexes = @Index(name = "message_dialog_id_idx", columnList = "dialog_id")
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull
  @Column(name = "sent_time", columnDefinition = "timestamp without time zone")
  private LocalDateTime sentTime;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "dialog_id", referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "message_dialog_fk")
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Dialog dialog;

  @NotNull
  @Column(name = "message_text", columnDefinition = "text")
  private String messageText;

  @NotNull
  @Column(name = "read_status", columnDefinition = "read_status")
  private ReadStatus readStatus;
}
