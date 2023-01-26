package ru.skillbox.zerone_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, columnDefinition = "timestamp without time zone")
    private LocalDateTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "post_author_fk")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User author;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String postText;

    @Column(nullable = false, columnDefinition = "timestamp without time zone")
    private LocalDateTime updateDate;

    @Column(nullable = false)
    private boolean isBlocked;

    @Column(nullable = false)
    private boolean isDeleted;

    @OneToMany(
            mappedBy = "post",
            fetch = FetchType.LAZY)
    private Set<BlockHistory> blockHistories  = new HashSet<>();

    @OneToMany(
            mappedBy = "post",
            fetch = FetchType.LAZY)
    private Set<Comment> comments  = new HashSet<>();
}
