package org.example.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.example.backend.dto.event.EventRequestDTO;
import org.example.backend.dto.event.EventResponseDTO;
import org.example.backend.repository.EventRepository;
import org.example.backend.repository.TokenRepository;
import org.example.backend.service.AuthService;
import org.example.backend.service.EventService;
import org.example.backend.service.JwtService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EventController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private EventRepository eventRepository;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private TokenRepository tokenRepository;

    private static final String TEST_TITLE = "Test title";
    private static final String TEST_DESCRIPTION = "Test description";
    private static final UUID TEST_ID = UUID.randomUUID();
    private static final String BASE_URL = "/api/v1/events";


    EventRequestDTO setupEventRequest() {
        return EventRequestDTO.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .build();
    }

    EventResponseDTO setupEventResponse() {
        return EventResponseDTO.builder()
                .id(TEST_ID)
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .feedbackCount(1L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    MockHttpServletRequestBuilder setupPostRequest() {
        return MockMvcRequestBuilders.post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    MockHttpServletRequestBuilder setupGetRequest() {
        return MockMvcRequestBuilders.get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    @Order(1)
    @Test
    @DisplayName("Create event success")
    @WithMockUser(value = "test-user", roles = {"ADMIN"})
    void testCreateEvent_whenValidDetailsProvided_returnsCorrectStatusAndResponseBody() throws Exception {
        // Arrange
        EventResponseDTO response = setupEventResponse();
        EventRequestDTO request = setupEventRequest();
        when(eventService.createEvent(request)).thenReturn(response);

        String json = objectMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(setupPostRequest().content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.description").value(request.getDescription()));

        // Verify
        verify(eventService, times(1)).createEvent(request);
    }

    @Order(2)
    @Test
    @DisplayName("Create event fails without admin role")
    @WithMockUser(value = "test-user", roles = {"USER"})
    void testCreateEvent_whenUserIsNotAdmin_accessIsDenied() throws Exception {
        // Arrange
        EventRequestDTO request = setupEventRequest();

        String json = objectMapper.writeValueAsString(request);

        // Act & Assert
        var thrown = assertThrows(ServletException.class,
                () -> mockMvc.perform(setupPostRequest().content(json)).andReturn());

        // Verify
        verify(eventService, never()).createEvent(any());
        assertInstanceOf(AuthorizationDeniedException.class, thrown.getCause());
    }
}