package org.example.backend.mapper;

import org.example.backend.dto.feedback.FeedbackResponseDTO;
import org.example.backend.model.Feedback;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {

    @Mapping(source = "event.id", target = "eventId")
    FeedbackResponseDTO toResponse(Feedback entity);
}
