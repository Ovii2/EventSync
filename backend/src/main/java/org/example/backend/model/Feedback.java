package org.example.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.enums.SentimentType;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "feedback")
@Entity
@Getter
@Setter
@ToString(exclude = {"event", "user"})
@EqualsAndHashCode(exclude = {"event", "user"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "feedback_content", columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private SentimentType sentimentType;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
