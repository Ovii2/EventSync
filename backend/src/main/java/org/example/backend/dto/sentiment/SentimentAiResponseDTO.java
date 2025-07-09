package org.example.backend.dto.sentiment;

import lombok.Data;

@Data
public class SentimentAiResponseDTO {

    private String label;
    private Double score;
}
