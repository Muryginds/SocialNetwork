package ru.skillbox.zerone.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.skillbox.zerone.backend.model.enumerated.MessagePermissions;
import ru.skillbox.zerone.backend.model.enumerated.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`user`")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull
  @Column(name = "first_name", columnDefinition = "text")
  private String firstName;

  @NotNull
  @Column(name = "last_name", columnDefinition = "text")
  private String lastName;

  @NotNull
  @Builder.Default
  @Column(name = "reg_date", columnDefinition = "timestamp without time zone")
  private LocalDateTime regDate = LocalDateTime.now();

  @Column(name = "birth_date")
  private LocalDate birthDate;

  @NotNull
  @Column(name = "email")
  private String email;

  @Column(name = "phone")
  private String phone;

  @NotNull
  @Column(name = "password")
  private String password;

  @Column(name = "photo")
  private String photo;

  @Column(name = "about", columnDefinition = "text")
  private String about;

  @Builder.Default
  @Column(name = "status", columnDefinition = "user_status default 'INACTIVE'")
  @Enumerated(EnumType.STRING)
  private UserStatus status = UserStatus.INACTIVE;

  @Column(name = "country")
  private String country;

  @Column(name = "city", nullable = false)
  private String city;

  @Column(name = "confirmation_code")
  private String confirmationCode;

  @Builder.Default
  @Column(name = "is_approved", columnDefinition = "boolean default false")
  private Boolean isApproved = false;

  @Builder.Default
  @Column(name = "message_permissions", columnDefinition = "message_permissions default 'ALL'")
  @Enumerated(EnumType.STRING)
  private MessagePermissions messagePermissions = MessagePermissions.ALL;

  @Column(name = "last_online_time", columnDefinition = "timestamp without time zone")
  private LocalDateTime lastOnlineTime;

  @Builder.Default
  @Column(name = "is_blocked", columnDefinition = "boolean default false")
  private Boolean isBlocked = false;

  @Builder.Default
  @Column(name = "is_deleted", columnDefinition = "boolean default false")
  private Boolean isDeleted = false;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private List<BlockHistory> blockHistories = new ArrayList<>();

  @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
  private  List<Comment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
  private List<Post> posts = new ArrayList<>();

  @OneToMany(mappedBy = "srcPerson", fetch = FetchType.LAZY)
  private List<Friendship> srcFiendships = new ArrayList<>();

  @OneToMany(mappedBy = "dstPerson", fetch = FetchType.LAZY)
  private List<Friendship> dstFriendships = new ArrayList<>();

  @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY)
  private List<Dialog> dialogSenders = new ArrayList<>();

  @OneToMany(mappedBy = "recipient", fetch = FetchType.LAZY)
  private List<Dialog> dialogRecipients = new ArrayList<>();

  @OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
  private List<Notification> notifications = new ArrayList<>();

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private List<NotificationSetting> notificationSettings = new ArrayList<>();

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private List<Like> likes = new ArrayList<>();


  public void addBlockHistory(BlockHistory blockHistory) {
    if (!blockHistories.contains(blockHistory)) {
      blockHistories.add(blockHistory);
      blockHistory.setUser(this);
    }
  }

  public void revmoveBlockHistory(BlockHistory blockHistory) {
    if (blockHistories.contains(blockHistory)) {
      blockHistories.remove(blockHistory);
      blockHistory.setUser(null);
    }
  }

  public void addComment(Comment comment) {
    if (!comments.contains(comment)) {
      comments.add(comment);
      comment.setAuthor(this);
    }
  }

  public void removeComment(Comment comment) {
    if (comments.contains(comment)) {
      comments.remove(comment);
      comment.setAuthor(null);
    }
  }

  public void addPost(Post post) {
    if (!posts.contains(post)) {
      posts.add(post);
      post.setAuthor(this);
    }
  }

  public void removePost(Post post) {
    if (posts.contains(post)) {
      posts.remove(post);
      post.setAuthor(null);
    }
  }

  public void addSrcFiendship(Friendship friendship) {
    if (!srcFiendships.contains(friendship)) {
      dstFriendships.add(friendship);
      friendship.setSrcPerson(this);
    }
  }

  public void removeSrcFiendship(Friendship friendship) {
    if (srcFiendships.contains(friendship)) {
      srcFiendships.remove(friendship);
      friendship.setSrcPerson(null);
    }
  }

  public void addDstFiendship(Friendship friendship) {
    if (!dstFriendships.contains(friendship)) {
      dstFriendships.add(friendship);
      friendship.setSrcPerson(this);
    }
  }

  public void removeDstFiendship(Friendship friendship) {
    if (dstFriendships.contains(friendship)) {
      dstFriendships.remove(friendship);
      friendship.setSrcPerson(null);
    }
  }

  public void addDialogSender(Dialog dialog) {
    if (!dialogSenders.contains(dialog)) {
      dialogSenders.add(dialog);
      dialog.setSender(this);
    }
  }

  public void removeDialogSender(Dialog dialog) {
    if (dialogSenders.contains(dialog)) {
      dialogSenders.remove(dialog);
      dialog.setSender(null);
    }
  }

  public void addDialogRecipient(Dialog dialog) {
    if (!dialogRecipients.contains(dialog)) {
      dialogRecipients.add(dialog);
      dialog.setRecipient(this);
    }
  }

  public void removeDialogRecipient(Dialog dialog) {
    if (dialogRecipients.contains(dialog)) {
      dialogRecipients.remove(dialog);
      dialog.setRecipient(null);
    }
  }

  public void addNotification(Notification notification) {
    if (!notifications.contains(notification)) {
      notifications.add(notification);
      notification.setPerson(this);
    }
  }

  public void removeNotification(Notification notification) {
    if (notifications.contains(notification)) {
      notifications.remove(notification);
      notification.setPerson(null);
    }
  }

  public void addNotificationSetting(NotificationSetting notificationSetting) {
    if (!notificationSettings.contains(notificationSetting)) {
      notificationSettings.add(notificationSetting);
      notificationSetting.setUser(this);
    }
  }

  public void removeNotificationSetting(NotificationSetting notificationSetting) {
    if (notificationSettings.contains(notificationSetting)) {
      notificationSettings.remove(notificationSetting);
      notificationSetting.setUser(null);
    }
  }

  public void addLike(Like like) {
    if (!likes.contains(like)) {
      likes.add(like);
      like.setUser(this);
    }
  }

  public void removeLike(Like like) {
    if (likes.contains(like)) {
      likes.remove(like);
      like.setUser(null);
    }
  }
}
