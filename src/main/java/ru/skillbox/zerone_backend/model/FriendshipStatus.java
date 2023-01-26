package ru.skillbox.zerone_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.skillbox.zerone_backend.enums.FriendshipCode;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class FriendshipStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, columnDefinition = "timestamp without time zone")
    private LocalDateTime time;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "friendship_code")
    @Enumerated(EnumType.STRING)
    private FriendshipCode code;

    @OneToMany(
            mappedBy = "status",
            fetch = FetchType.LAZY)
    private Set<Friendship> friendships = new HashSet<>();

}
