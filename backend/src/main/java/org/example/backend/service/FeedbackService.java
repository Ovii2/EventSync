package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.feedback.FeedbackRequestDTO;
import org.example.backend.dto.feedback.FeedbackResponseDTO;
import org.example.backend.dto.summary.FeedbackSummaryResponseDTO;
import org.example.backend.enums.SentimentType;
import org.example.backend.exception.NotFoundException;
import org.example.backend.mapper.FeedbackMapper;
import org.example.backend.model.Event;
import org.example.backend.model.Feedback;
import org.example.backend.model.User;
import org.example.backend.repository.EventRepository;
import org.example.backend.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final EventRepository eventRepository;
    private final FeedbackMapper feedbackMapper;
    private final UserService userService;

    public FeedbackResponseDTO submitFeedback(UUID eventId, FeedbackRequestDTO feedbackRequestDTO) {
        Event event = checkIfEventExists(eventId);

        User user = userService.getCurrentUser()
                .orElseThrow(() -> new NotFoundException("Authenticated user not found"));

        Feedback feedback = Feedback.builder()
                .content(feedbackRequestDTO.getContent())
                .event(event)
                .user(user)
                .build();

        feedbackRepository.save(feedback);
        return feedbackMapper.toResponse(feedback);
    }

    public FeedbackSummaryResponseDTO getFeedbackSummary(UUID eventId) {
        Event event = checkIfEventExists(eventId);

        Long positive = feedbackRepository.countByEventAndSentimentType(event, SentimentType.POSITIVE);
        Long neutral = feedbackRepository.countByEventAndSentimentType(event, SentimentType.NEUTRAL);
        Long negative = feedbackRepository.countByEventAndSentimentType(event, SentimentType.NEGATIVE);
        Long total = feedbackRepository.countByEvent(event);

        return FeedbackSummaryResponseDTO.builder()
                .eventId(event.getId())
                .totalFeedbackCount(total)
                .positiveCount(positive)
                .neutralCount(neutral)
                .negativeCount(negative)
                .build();
    }

    public Event checkIfEventExists(UUID eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event was not found"));
    }
}
