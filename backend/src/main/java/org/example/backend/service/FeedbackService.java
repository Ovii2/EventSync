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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final EventRepository eventRepository;
    private final FeedbackMapper feedbackMapper;
    private final UserService userService;
    private final AiService aiService;
    private final SimpMessagingTemplate messagingTemplate;


    @Transactional
    public FeedbackResponseDTO submitFeedback(UUID eventId, FeedbackRequestDTO feedbackRequestDTO) {
        Event event = checkIfEventExists(eventId);

        User user = userService.getCurrentUser()
                .orElseThrow(() -> new NotFoundException("Authenticated user not found"));

        Feedback feedback = Feedback.builder()
                .content(feedbackRequestDTO.getContent())
                .event(event)
                .sentimentType(SentimentType.PENDING)
                .user(user)
                .build();

       Feedback saved = feedbackRepository.save(feedback);

        analyzeAndUpdateSentimentAsync(saved);
        return feedbackMapper.toResponse(feedback);
    }

    public FeedbackSummaryResponseDTO getEventFeedbackSummaryById(UUID eventId) {
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

    public List<FeedbackResponseDTO> getFeedbackByEventId(UUID eventId) {
        Event event = checkIfEventExists(eventId);
        List<Feedback> feedbackList = feedbackRepository.findAllByEventOrderByCreatedAtDesc(event);

        return feedbackList.stream()
                .map(feedbackMapper::toResponse)
                .toList();
    }

    public Event checkIfEventExists(UUID eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event was not found"));
    }

    public void analyzeAndUpdateSentimentAsync(Feedback feedback) {
        CompletableFuture.runAsync(() -> {
            SentimentType sentiment = aiService.analyzeSentiment(feedback.getContent()).join();
            feedback.setSentimentType(sentiment);
            feedbackRepository.save(feedback);
            messagingTemplate.convertAndSend("/topic/feedback-updates", feedbackMapper.toResponse(feedback));
        });
    }
}
