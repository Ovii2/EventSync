package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.event.EventRequestDTO;
import org.example.backend.dto.event.EventResponseDTO;
import org.example.backend.exception.AlreadyExistsException;
import org.example.backend.exception.NotFoundException;
import org.example.backend.exception.UserNotAuthenticatedException;
import org.example.backend.mapper.EventMapper;
import org.example.backend.model.Event;
import org.example.backend.model.User;
import org.example.backend.repository.EventRepository;
import org.example.backend.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final FeedbackRepository feedbackRepository;
    private final EventMapper eventMapper;
    private final UserService userService;

    public EventResponseDTO createEvent(EventRequestDTO eventRequestDTO) {
        if (eventRepository.existsByTitle(eventRequestDTO.getTitle())) {
            throw new AlreadyExistsException("Event title already exists");
        }

        User currentUser = userService.getCurrentUser()
                .orElseThrow(() -> new UserNotAuthenticatedException("User not authenticated"));

        Event event = eventMapper.toEntity(eventRequestDTO);
        event.setUser(currentUser);
        eventRepository.save(event);
        return eventMapper.toResponse(event);
    }

    public EventResponseDTO getEventById(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found with ID: " + eventId));
        return eventMapper.toResponse(event);
    }

    public List<EventResponseDTO> getAllEvents() {
        return eventRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(event -> {
                    EventResponseDTO dto = eventMapper.toResponse(event);
                    Long count = feedbackRepository.countByEvent(event);
                    dto.setFeedbackCount(count);
                    dto.setCreatedAt(event.getCreatedAt());
                    return dto;
                })
                .toList();
    }
}
