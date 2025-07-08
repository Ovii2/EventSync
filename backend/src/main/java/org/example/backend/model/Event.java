package org.example.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Table(name = "events")
@Entity
@Getter
@Setter
@ToString(exclude = "feedbackList")
@EqualsAndHashCode(exclude = "feedbackList")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "event_title")
    private String title;

    @Column(name = "event_description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Feedback> feedbackList;
}
