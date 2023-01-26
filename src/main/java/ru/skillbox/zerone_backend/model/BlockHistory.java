package ru.skillbox.zerone_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.skillbox.zerone_backend.enums.ActionType;

import java.time.LocalDateTime;

@Entity
@Data
public class BlockHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private LocalDateTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "block_history_user_fk")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "block_history_post_fk")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "block_history_comment_fk")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "action_type")
    private ActionType action;
}
