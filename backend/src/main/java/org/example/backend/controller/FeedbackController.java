package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.feedback.FeedbackRequestDTO;
import org.example.backend.dto.feedback.FeedbackResponseDTO;
import org.example.backend.dto.summary.FeedbackSummaryResponseDTO;
import org.example.backend.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1/events")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/{eventId}/feedback")
    public ResponseEntity<FeedbackResponseDTO> submitFeedback(@PathVariable UUID eventId, @Valid @RequestBody FeedbackRequestDTO feedbackRequestDTO) {
        return new ResponseEntity<>(feedbackService.submitFeedback(eventId, feedbackRequestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{eventId}/summary")
    public ResponseEntity<FeedbackSummaryResponseDTO> getEventFeedbackSummary(@PathVariable UUID eventId) {
        return ResponseEntity.ok(feedbackService.getFeedbackSummary(eventId));
    }

    @GetMapping("/{eventId}/feedback")
    public ResponseEntity<List<FeedbackResponseDTO>> getFeedbackByEventId(@PathVariable UUID eventId) {
        return ResponseEntity.ok(feedbackService.getFeedbackByEventId(eventId));
    }
}
