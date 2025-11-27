package org.example.backend.service;

import org.example.backend.dto.feedback.FeedbackRequestDTO;
import org.example.backend.dto.feedback.FeedbackResponseDTO;
import org.example.backend.enums.SentimentType;
import org.example.backend.enums.UserRole;
import org.example.backend.exception.NotFoundException;
import org.example.backend.mapper.FeedbackMapper;
import org.example.backend.model.Event;
import org.example.backend.model.Feedback;
import org.example.backend.model.User;
import org.example.backend.repository.EventRepository;
import org.example.backend.repository.FeedbackRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Spy
    @InjectMocks
    private FeedbackService feedbackService;

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private FeedbackMapper feedbackMapper;

    @Mock
    private UserService userService;

    @Mock
    private AiService aiService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private static final String TEST_CONTENT = "valid-content";
    private static final UUID TEST_EVENT_ID = UUID.randomUUID();

    FeedbackRequestDTO setupFeedbackRequest() {
        return FeedbackRequestDTO.builder()
                .content(TEST_CONTENT)
                .build();
    }

    FeedbackResponseDTO setupFeedbackResponse() {
        return FeedbackResponseDTO.builder()
                .id(UUID.randomUUID())
                .eventId(UUID.randomUUID())
                .content(TEST_CONTENT)
                .createdAt(LocalDateTime.now())
                .sentimentType(SentimentType.POSITIVE)
                .build();
    }

    Feedback setupFeedback() {
        return Feedback.builder()
                .id(UUID.randomUUID())
                .content(TEST_CONTENT)
                .sentimentType(SentimentType.POSITIVE)
                .event(setupEvent())
                .build();
    }

    Event setupEvent() {
        return Event.builder()
                .id(TEST_EVENT_ID)
                .title("valid-title")
                .description("valid-description")
                .createdAt(LocalDateTime.now())
                .build();
    }

    User setupUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .username("Test")
                .email("test@email.com")
                .password("12345678")
                .role(UserRole.ROLE_USER)
                .build();
    }


    @Order(1)
    @Test
    @DisplayName("Can submit feedback")
    void testSubmitFeedback_whenValidDetailsProvided_returnsFeedbackResponse() {
        // Arrange
        FeedbackRequestDTO request = setupFeedbackRequest();
        Feedback savedFeedback = setupFeedback();
        Optional<User> user = Optional.ofNullable(setupUser());
        Event event = setupEvent();

        when(eventRepository.findById(TEST_EVENT_ID)).thenReturn(Optional.of(event));
        when(userService.getCurrentUser()).thenReturn(user);
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(savedFeedback);
        when(feedbackMapper.toResponse(any(Feedback.class))).thenReturn(FeedbackResponseDTO.builder()
                .id(savedFeedback.getId())
                .content(savedFeedback.getContent())
                .sentimentType(savedFeedback.getSentimentType())
                .eventId(TEST_EVENT_ID)
                .build());
        doNothing().when(feedbackService).analyzeAndUpdateSentimentAsync(any(Feedback.class));

        // Act
        FeedbackResponseDTO feedback = feedbackService.submitFeedback(TEST_EVENT_ID, request);

        // Assert
        assertNotNull(feedback);
        assertNotNull(feedback.getId());
        assertEquals(feedback.getContent(), savedFeedback.getContent());
        assertEquals(feedback.getSentimentType(), savedFeedback.getSentimentType());

        // Verify
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Order(2)
    @Test
    @DisplayName("Event not found")
    void testSubmitFeedback_whenEventIsNotFound_throwsNotFoundException() {
        // Arrange
        FeedbackRequestDTO request = setupFeedbackRequest();

        when(eventRepository.findById(TEST_EVENT_ID)).thenReturn(Optional.empty());

        // Act
        var thrown = assertThrows(NotFoundException.class, () -> feedbackService.submitFeedback(TEST_EVENT_ID, request));

        // Assert
        assertNotNull(thrown);

        // Verify
        verify(userService, never()).getCurrentUser();
        verify(feedbackRepository, never()).save(any(Feedback.class));
        verify(feedbackMapper, never()).toResponse(any(Feedback.class));
        verify(eventRepository, times(1)).findById(TEST_EVENT_ID);
    }

    @Order(3)
    @Test
    @DisplayName("Can get event summary")
    void testGetEventFeedbackSummaryById_whenFeedbackExists_returnsFeedbackSummaryResponse() {
        // Arrange
        Event event = setupEvent();

        when(eventRepository.findById(TEST_EVENT_ID)).thenReturn(Optional.of(event));
        when(feedbackRepository.countByEventAndSentimentType(event, SentimentType.NEUTRAL)).thenReturn(1L);
        when(feedbackRepository.countByEventAndSentimentType(event, SentimentType.POSITIVE)).thenReturn(1L);
        when(feedbackRepository.countByEventAndSentimentType(event, SentimentType.NEGATIVE)).thenReturn(1L);
        when(feedbackRepository.countByEvent(event)).thenReturn(3L);

        // Act
        var summary = feedbackService.getEventFeedbackSummaryById(TEST_EVENT_ID);

        // Assert
        assertNotNull(summary.getEventId());
        assertEquals(TEST_EVENT_ID, summary.getEventId());
        assertEquals(1L, summary.getPositiveCount());
        assertEquals(1L, summary.getNeutralCount());
        assertEquals(1L, summary.getNegativeCount());
        assertEquals(3L, summary.getTotalFeedbackCount());

        // Verify
        verify(feedbackRepository, times(1)).countByEventAndSentimentType(event, SentimentType.NEUTRAL);
        verify(feedbackRepository, times(1)).countByEventAndSentimentType(event, SentimentType.POSITIVE);
        verify(feedbackRepository, times(1)).countByEventAndSentimentType(event, SentimentType.NEGATIVE);
        verify(feedbackRepository, times(1)).countByEvent(event);
    }

    @Order(4)
    @Test
    @DisplayName("Get summary fails, event does not exist")
    void testGetEventFeedbackSummaryById_whenFeedbackDoesNotExist_throwsNotFoundException() {
        // Arrange
        when(eventRepository.findById(TEST_EVENT_ID)).thenReturn(Optional.empty());

        // Act
        var thrown = assertThrows(NotFoundException.class, () -> feedbackService.getEventFeedbackSummaryById(TEST_EVENT_ID));

        // Assert
        assertNotNull(thrown);

        // Verify
        verify(feedbackRepository, never()).countByEventAndSentimentType(any(Event.class), eq(SentimentType.NEUTRAL));
        verify(feedbackRepository, never()).countByEventAndSentimentType(any(Event.class), eq(SentimentType.POSITIVE));
        verify(feedbackRepository, never()).countByEventAndSentimentType(any(Event.class), eq(SentimentType.NEGATIVE));
        verify(feedbackRepository, never()).countByEvent(any(Event.class));
    }

}