package org.example.backend.dto.summary;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeedbackSummaryResponseDTO {

    private UUID eventId;
    private Long totalFeedbackCount;
    private Long positiveCount;
    private Long neutralCount;
    private Long negativeCount;
}
