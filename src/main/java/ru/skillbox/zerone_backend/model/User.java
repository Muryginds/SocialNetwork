package ru.skillbox.zerone_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.skillbox.zerone_backend.enumerated.MessagePermissions;
import ru.skillbox.zerone_backend.enumerated.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "`user`")
@Data
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @NotNull
  @Column(columnDefinition = "text")
  private String firstName;

  @NotNull
  @Column(columnDefinition = "text")
  private String lastName;

  @NotNull
  @Column(columnDefinition = "timestamp without time zone")
  private LocalDateTime regDate;

  @Column(nullable = false)
  private LocalDate birthDate;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String phone;

  @Column(nullable = false)
  private String password;

  private String photo;

  @Column(columnDefinition = "text")
  private String about;

  @NotNull
  @Column(columnDefinition = "user_status")
  @Enumerated(EnumType.STRING)
  private UserStatus status;

  @Column(nullable = false)
  private String country;

  @Column(nullable = false)
  private String city;

  @Column(nullable = false)
  private String confirmationCode;

  @Column(nullable = false)
  private boolean isApproved;

  @NotNull
  @Column(columnDefinition = "message_permissions")
  @Enumerated(EnumType.STRING)
  private MessagePermissions messagePermissions;

  @Column(columnDefinition = "timestamp without time zone")
  private LocalDateTime lastOnlineTime;

  @Column(nullable = false)
  private boolean isBlocked;

  @Column(nullable = false)
  private boolean isDeleted;

  @OneToMany(
      mappedBy = "user",
      fetch = FetchType.LAZY)
  private Set<BlockHistory> blockHistories = new HashSet<>();

  @OneToMany(
      mappedBy = "author",
      fetch = FetchType.LAZY)
  private Set<Comment> comments = new HashSet<>();

  @OneToMany(
      mappedBy = "author",
      fetch = FetchType.LAZY)
  private Set<Post> posts = new HashSet<>();

  @OneToMany(
      mappedBy = "srcPerson",
      fetch = FetchType.LAZY)
  private Set<Friendship> srcFiendships = new HashSet<>();

  @OneToMany(
      mappedBy = "dstPerson",
      fetch = FetchType.LAZY)
  private Set<Friendship> dstFriendships = new HashSet<>();
}
