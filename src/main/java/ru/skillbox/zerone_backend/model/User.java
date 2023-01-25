package ru.skillbox.zerone_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;
import ru.skillbox.zerone_backend.enums.MessagePermissions;
import ru.skillbox.zerone_backend.enums.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "`user`")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, columnDefinition = "text")
    private String firstName;

    @Column(nullable = false, columnDefinition = "text")
    private String lastName;

    @Column(nullable = false, columnDefinition = "timestamp without time zone")
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

    @Column(columnDefinition =  "text")
    private String about;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessagePermissions messagePermissions;

    @Column(columnDefinition = "timestamp without time zone")
    private LocalDateTime lastOnlineTime;

    private boolean isBlocked;

    private boolean isDeleted;
}
