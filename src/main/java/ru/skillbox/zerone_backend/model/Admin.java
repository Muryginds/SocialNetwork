package ru.skillbox.zerone_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.skillbox.zerone_backend.enums.UserStatus;
import ru.skillbox.zerone_backend.enums.UserType;

@Entity
@Data
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "user_type")
    private UserType type;
}
