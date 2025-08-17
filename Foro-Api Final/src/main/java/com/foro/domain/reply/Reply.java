package com.foro.domain.reply;

import com.foro.domain.topic.Topic;
import com.foro.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "replies")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Reply {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(optional = false) @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(optional = false) @JoinColumn(name = "topic_id")
    private Topic topic;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
