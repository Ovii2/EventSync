package org.example.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.backend.dto.feedback.FeedbackRequestDTO;
import org.example.backend.dto.feedback.FeedbackResponseDTO;
import org.example.backend.enums.SentimentType;
import org.example.backend.filter.JwtAuthenticationFilter;
import org.example.backend.repository.TokenRepository;
import org.example.backend.service.AuthService;
import org.example.backend.service.FeedbackService;
import org.example.backend.service.JwtService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FeedbackController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FeedbackService feedbackService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private TokenRepository tokenRepository;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String TEST_CONTENT = "valid-content";
    private static final UUID TEST_EVENT_ID = UUID.randomUUID();
    private static final String BASE_URL = "/api/v1/events/%s".formatted(TEST_EVENT_ID);

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

    MockHttpServletRequestBuilder setupPostRequest(String path) {
        return MockMvcRequestBuilders.post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    MockHttpServletRequestBuilder setupGetRequest(String path) {
        return MockMvcRequestBuilders.get(path)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    @Order(1)
    @Test
    @DisplayName("Can submit feedback")
    void testSubmitFeedback_whenValidDetailsProvided_returnsCorrectStatusAndResponseBody() throws Exception {
        // Arrange
        FeedbackRequestDTO request = setupFeedbackRequest();
        FeedbackResponseDTO response = setupFeedbackResponse();
        when(feedbackService.submitFeedback(TEST_EVENT_ID, request)).thenReturn(response);

        String json = objectMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(setupPostRequest(BASE_URL + "/feedback").content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.content").value(response.getContent()));


        // Verify
        verify(feedbackService, times(1)).submitFeedback(TEST_EVENT_ID, request);
    }

    @Order(2)
    @Test
    @DisplayName("Submit feedback fails when content is blank")
    void testSubmitFeedback_whenContentIsEmpty_returnsBadRequest() throws Exception {
        // Arrange
        FeedbackRequestDTO request = setupFeedbackRequest();
        request.setContent(" ");

        String json = objectMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(setupPostRequest(BASE_URL + "/feedback").content(json))
                .andExpect(status().isBadRequest());

        // Verify
        verify(feedbackService, never()).submitFeedback(eq(TEST_EVENT_ID), any(FeedbackRequestDTO.class));
    }

}