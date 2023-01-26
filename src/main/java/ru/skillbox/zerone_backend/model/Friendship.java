package ru.skillbox.zerone_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Data
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "friendship_friendship_status_fk")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private FriendshipStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "src_person_id", nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "friendship_src_person_fk")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User srcPerson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dst_person_id", nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "friendship_dst_person_fk")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User dstPerson;

    @NotNull
    @Column(columnDefinition = "timestamp without time zone")
    private LocalDateTime time;
}
