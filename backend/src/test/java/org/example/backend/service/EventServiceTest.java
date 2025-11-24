package org.example.backend.service;

import org.example.backend.dto.event.EventRequestDTO;
import org.example.backend.dto.event.EventResponseDTO;
import org.example.backend.enums.UserRole;
import org.example.backend.exception.AlreadyExistsException;
import org.example.backend.exception.UserNotAuthenticatedException;
import org.example.backend.mapper.EventMapper;
import org.example.backend.model.Event;
import org.example.backend.model.User;
import org.example.backend.repository.EventRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private UserService userService;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private EventRepository eventRepository;

    private static final String TEST_TITLE = "Test title";
    private static final String TEST_DESCRIPTION = "Test description";

    EventRequestDTO setupEventRequest() {
        return EventRequestDTO.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .build();
    }

    EventResponseDTO setupEventResponse() {
        return EventResponseDTO.builder()
                .id(UUID.randomUUID())
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .feedbackCount(1L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    Event setupEvent() {
        return Event.builder()
                .id(UUID.randomUUID())
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
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
    @DisplayName("Event can be created")
    void testCreateEvent_whenValidDetailsProvided_returnsEventResponse() {
        // Arrange
        EventRequestDTO request = setupEventRequest();
        Event savedEvent = setupEvent();
        Optional<User> user = Optional.ofNullable(setupUser());

        when(userService.getCurrentUser()).thenReturn(user);
        when(eventMapper.toEntity(request)).thenReturn(savedEvent);
        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);
        when(eventRepository.existsByTitle(TEST_TITLE)).thenReturn(false);
        when(eventMapper.toResponse(savedEvent)).thenReturn(EventResponseDTO.builder()
                .id(savedEvent.getId())
                .title(savedEvent.getTitle())
                .description(savedEvent.getDescription())
                .createdAt(LocalDateTime.now())
                .build());

        // Act
        EventResponseDTO event = eventService.createEvent(request);

        // Assert
        assertNotNull(event);
        assertNotNull(event.getId());
        assertEquals(request.getTitle(), event.getTitle());
        assertEquals(request.getDescription(), event.getDescription());

        // Verify
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Order(2)
    @Test
    @DisplayName("Create event fails, title exists")
    void testCreateEvent_whenEventTitleExists_throwsAlreadyExistsException() {
        // Arrange
        EventRequestDTO request = setupEventRequest();
        when(eventRepository.existsByTitle(request.getTitle())).thenReturn(true);

        // Act
        var thrown = assertThrows(AlreadyExistsException.class, () -> eventService.createEvent(request));

        // Assert
        assertEquals("Event title already exists", thrown.getMessage());

        // Verify
        verify(userService, never()).getCurrentUser();
        verify(eventRepository, never()).save(any(Event.class));
        verify(eventMapper, never()).toEntity(any(EventRequestDTO.class));
        verify(eventRepository, times(1)).existsByTitle(request.getTitle());
    }

    @Order(3)
    @Test
    @DisplayName("Create event fails, user not authenticated")
    void testCreateEvent_whenUserIsNotAuthenticated_throwsUserNotAuthenticatedException() {
        // Arrange
        EventRequestDTO request = setupEventRequest();
        when(userService.getCurrentUser()).thenReturn(Optional.empty());

        // Act
        var thrown = assertThrows(UserNotAuthenticatedException.class, () -> eventService.createEvent(request));

        // Assert
        assertEquals("User not authenticated", thrown.getMessage());

        // Verify
        verify(eventRepository, never()).save(any(Event.class));
    }

}