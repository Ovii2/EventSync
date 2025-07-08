package org.example.backend.mapper;

import org.example.backend.dto.feedback.FeedbackResponseDTO;
import org.example.backend.model.Feedback;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {

    FeedbackResponseDTO toResponse(Feedback entity);
}
