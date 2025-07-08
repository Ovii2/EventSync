package org.example.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.enums.UserRole;

import java.util.List;
import java.util.UUID;

@Table(name = "users")
@Entity
@Getter
@Setter
@ToString(exclude = {"feedbackList", "eventList"})
@EqualsAndHashCode(exclude = {"feedbackList", "eventList"})
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

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Feedback> feedbackList;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Event> eventList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Token> tokens;
}
