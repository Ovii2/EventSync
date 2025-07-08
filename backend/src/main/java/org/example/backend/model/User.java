package org.example.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Table(name = "users")
@Entity
@Getter
@Setter
@ToString(exclude = "feedbackList")
@EqualsAndHashCode(exclude = "feedbackList")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Feedback> feedbackList;
}
