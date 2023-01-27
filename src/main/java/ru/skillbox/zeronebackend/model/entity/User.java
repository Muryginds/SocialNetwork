package ru.skillbox.zeronebackend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.zeronebackend.model.enumerated.MessagePermissions;
import ru.skillbox.zeronebackend.model.enumerated.UserStatus;

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
  private long id;

  @NotNull
  @Column(name = "first_name", columnDefinition = "text")
  private String firstName;

  @NotNull
  @Column(name = "last_name", columnDefinition = "text")
  private String lastName;

  @NotNull
  @Column(name = "reg_date", columnDefinition = "timestamp without time zone")
  private LocalDateTime regDate;

  @NotNull
  @Column(name = "birth_date")
  private LocalDate birthDate;

  @NotNull
  @Column(name = "email")
  private String email;

  @NotNull
  @Column(name = "phone")
  private String phone;

  @NotNull
  @Column(name = "password")
  private String password;

  @Column(name = "photo")
  private String photo;

  @Column(name = "about", columnDefinition = "text")
  private String about;

  @Column(name = "status", columnDefinition = "user_status default 'INACTIVE'")
  @Enumerated(EnumType.STRING)
  private UserStatus status;

  @NotNull
  @Column(name = "country")
  private String country;

  @Column(name = "city", nullable = false)
  private String city;

  @NotNull
  @Column(name = "confirmation_code")
  private String confirmationCode;

  @Column(name = "is_approved", columnDefinition = "boolean default false")
  private boolean isApproved;

  @Column(name = "message_permissions", columnDefinition = "message_permissions default 'ALL'")
  @Enumerated(EnumType.STRING)
  private MessagePermissions messagePermissions;

  @Column(name = "last_online_time", columnDefinition = "timestamp without time zone")
  private LocalDateTime lastOnlineTime;

  @Column(name = "is_blocked", columnDefinition = "boolean default false")
  private boolean isBlocked;

  @Column(name = "is_deleted", columnDefinition = "boolean default false")
  private boolean isDeleted;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private List<BlockHistory> blockHistories = new ArrayList<>();

  @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
  private  List<Comment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
  private List<Post> posts = new ArrayList<>();

  @OneToMany(mappedBy = "srcPerson", fetch = FetchType.LAZY)
  private List<Friendship> srcFriendships = new ArrayList<>();

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
}
