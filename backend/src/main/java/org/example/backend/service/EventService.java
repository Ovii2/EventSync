package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.event.EventRequestDTO;
import org.example.backend.dto.event.EventResponseDTO;
import org.example.backend.exception.AlreadyExistsException;
import org.example.backend.mapper.EventMapper;
import org.example.backend.model.Event;
import org.example.backend.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventResponseDTO createEvent(EventRequestDTO eventRequestDTO) {
        if (eventRepository.existsByTitle(eventRequestDTO.getTitle())) {
            throw new AlreadyExistsException("Event title already exists");
        }

        Event event = eventMapper.toEntity(eventRequestDTO);
        eventRepository.save(event);
        return eventMapper.toResponse(event);
    }

    public List<EventResponseDTO> getAllEvents() {
        return eventRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(eventMapper::toResponse)
                .toList();
    }
}
