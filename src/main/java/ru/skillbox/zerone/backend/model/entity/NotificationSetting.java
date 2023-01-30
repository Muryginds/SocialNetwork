package ru.skillbox.zerone.backend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "notification_setting",
    indexes = @Index(name = "notification_setting_user_id_idx", columnList = "user_id")
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSetting {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "notification_setting_user_fk")
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User user;

  @NotNull
  @Column(name = "post_enabled")
  private Boolean postEnabled;

  @NotNull
  @Column(name = "post_comment_enabled")
  private Boolean postCommentEnabled;

  @NotNull
  @Column(name = "comment_comment_enabled")
  private Boolean commentCommentEnabled;

  @NotNull
  @Column(name = "friend_request_enabled")
  private Boolean friendRequestEnabled;

  @NotNull
  @Column(name = "messages_enabled")
  private Boolean messagesEnabled;

  @NotNull
  @Column(name = "friend_birthday_enabled")
  private Boolean friendBirthdayEnabled;
}
