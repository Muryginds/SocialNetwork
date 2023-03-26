package ru.skillbox.zerone.backend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.zerone.backend.model.enumerated.ReadStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "message")
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
  @Builder.Default
  @Column(name = "sent_time", columnDefinition = "timestamp without time zone")
  private LocalDateTime sentTime = LocalDateTime.now();

  @NotNull
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "dialog_id", referencedColumnName = "id")
  private Dialog dialog;

  @NotNull
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "author_id", nullable = false, referencedColumnName = "id")
  private User author;

  @NotNull
  @NotBlank
  @Column(name = "message_text", columnDefinition = "text")
  private String messageText;

  @NotNull
  @Builder.Default
  @Column(name = "read_status", columnDefinition = "read_status default 'SENT'")
  @Enumerated(EnumType.STRING)
  private ReadStatus readStatus = ReadStatus.SENT;
}
