package org.example.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.dto.sentiment.SentimentAiResponseDTO;
import org.example.backend.enums.SentimentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiService {

    private final WebClient webClient;

    @Value("${api.key}")
    private String apiKey;

    @Value("${api.url}")
    private String apiUrl;

    @Async
    public CompletableFuture<SentimentType> analyzeSentiment(String text) {
        try {
            String response = webClient.post()
                    .uri(apiUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("inputs", text))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Raw API response: {}", response);

            return CompletableFuture.completedFuture(parseSentiment(response));
        } catch (WebClientResponseException e) {
            log.error("AI sentiment call failed: {}", e.getMessage());
            return CompletableFuture.completedFuture(SentimentType.NEUTRAL);
        }
    }

    private SentimentType parseSentiment(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<List<SentimentAiResponseDTO>> predictions = mapper.readValue(
                    response, new TypeReference<>() {
                    }
            );

            SentimentAiResponseDTO top = predictions.getFirst().stream()
                    .max(Comparator.comparingDouble(SentimentAiResponseDTO::getScore))
                    .orElseThrow();

            return switch (top.getLabel().toLowerCase()) {
                case "positive" -> SentimentType.POSITIVE;
                case "negative" -> SentimentType.NEGATIVE;
                default -> SentimentType.NEUTRAL;
            };
        } catch (JsonProcessingException e) {
            log.error("Failed to parse sentiment: {}", e.getMessage());
            return SentimentType.NEUTRAL;
        }
    }
}