package org.example.backend.repository;

import org.example.backend.enums.SentimentType;
import org.example.backend.model.Event;
import org.example.backend.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {

    Long countByEventAndSentimentType(Event event, SentimentType sentimentType);
    Long countByEvent(Event event);
}
