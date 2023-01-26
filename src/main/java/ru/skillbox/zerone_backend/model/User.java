package ru.skillbox.zerone_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.skillbox.zerone_backend.model.enumerated.MessagePermissions;
import ru.skillbox.zerone_backend.model.enumerated.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`user`")
@Data
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

  @Column(name = "birth_date", nullable = false)
  private LocalDate birthDate;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "phone", nullable = false)
  private String phone;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "photo")
  private String photo;

  @Column(name = "about", columnDefinition = "text")
  private String about;

  @NotNull
  @Column(name = "status", columnDefinition = "user_status")
  @Enumerated(EnumType.STRING)
  private UserStatus status;

  @Column(name = "country", nullable = false)
  private String country;

  @Column(name = "city", nullable = false)
  private String city;

  @Column(name = "confirmation_code", nullable = false)
  private String confirmationCode;

  @Column(name = "is_approved", nullable = false)
  private boolean isApproved;

  @NotNull
  @Column(name = "message_permissions", columnDefinition = "message_permissions")
  @Enumerated(EnumType.STRING)
  private MessagePermissions messagePermissions;

  @Column(name = "last_online_time", columnDefinition = "timestamp without time zone")
  private LocalDateTime lastOnlineTime;

  @Column(name = "is_blocked", nullable = false)
  private boolean isBlocked;

  @Column(name = "is_deleted", nullable = false)
  private boolean isDeleted;

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
}
