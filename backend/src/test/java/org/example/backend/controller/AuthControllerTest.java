package org.example.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.dto.login.LoginRequestDTO;
import org.example.backend.dto.login.LoginResponseDTO;
import org.example.backend.dto.user.UserRequestDTO;
import org.example.backend.dto.user.UserResponseDTO;
import org.example.backend.enums.UserRole;
import org.example.backend.repository.TokenRepository;
import org.example.backend.service.AuthService;
import org.example.backend.service.JwtService;
import org.example.backend.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private TokenRepository tokenRepository;

    private static final String TEST_USERNAME = "Test";
    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_PASSWORD = "12345678";
    private static final String TEST_TOKEN = "test-jwt-token";
    private static final String BASE_URL = "/api/v1/auth";


    UserRequestDTO buildUserRequestDTO() {
        return UserRequestDTO.builder()
                .username(TEST_USERNAME)
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();
    }

    UserResponseDTO buildUserResponseDTO() {
        return UserResponseDTO.builder()
                .id(UUID.randomUUID())
                .username(TEST_USERNAME)
                .email(TEST_EMAIL)
                .role(UserRole.ROLE_USER)
                .build();
    }

    LoginRequestDTO buildLoginRequestDTO() {
        return LoginRequestDTO.builder()
                .username(TEST_USERNAME)
                .password(TEST_PASSWORD)
                .build();
    }

    LoginResponseDTO buildLoginResponseDTO() {
        return LoginResponseDTO.builder()
                .token(TEST_TOKEN)
                .build();
    }

    MockHttpServletRequestBuilder buildPostRequest(String path) {
        return MockMvcRequestBuilders.post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    @Order(1)
    @Test
    @DisplayName("Create user")
    void testCreateUser_whenValidUserDetailsProvided_returnsCorrectStatusAndResponseBody() throws Exception {
        // Arrange
        UserResponseDTO responseDTO = buildUserResponseDTO();
        UserRequestDTO requestDTO = buildUserRequestDTO();
        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(responseDTO);
        String json = new ObjectMapper().writeValueAsString(requestDTO);

        // Act
        MvcResult mvcResult = mockMvc.perform(buildPostRequest(BASE_URL + "/register").content(json)).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        UserResponseDTO createdUser = new ObjectMapper().readValue(responseBodyAsString, UserResponseDTO.class);

        // Assert
        assertNotNull(createdUser.getId());
        assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus());
        assertEquals(requestDTO.getUsername(), createdUser.getUsername());
        assertEquals(requestDTO.getEmail(), createdUser.getEmail());
        assertEquals(UserRole.ROLE_USER, createdUser.getRole());

        // Verify
        verify(userService, times(1)).createUser(any(UserRequestDTO.class));
    }

    @Order(2)
    @Test
    @DisplayName("Create user without username")
    void testCreateUser_whenUsernameIsNotProvided_returnsBadRequest() throws Exception {
        // Arrange
        UserRequestDTO requestDTO = buildUserRequestDTO();
        requestDTO.setUsername(" ");
        String json = new ObjectMapper().writeValueAsString(requestDTO);

        // Act
        MvcResult mvcResult = mockMvc.perform(buildPostRequest(BASE_URL + "/register").content(json)).andReturn();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());

        // Verify
        verify(userService, never()).createUser(any(UserRequestDTO.class));
    }

    @Order(3)
    @Test
    @DisplayName("Create user without password")
    void testCreateUser_whenPasswordIsNotProvided_returnsBadRequest() throws Exception {
        // Arrange
        UserRequestDTO requestDTO = buildUserRequestDTO();
        requestDTO.setPassword(" ");
        String json = new ObjectMapper().writeValueAsString(requestDTO);

        // Act
        MvcResult mvcResult = mockMvc.perform(buildPostRequest(BASE_URL + "/register").content(json)).andReturn();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());

        // Verify
        verify(userService, never()).createUser(any(UserRequestDTO.class));
    }

    @Order(4)
    @Test
    @DisplayName("Login valid user")
    void testLoginUser_withValidCredentials_returnsCorrectStatusAndToken() throws Exception {
        // Arrange
        LoginRequestDTO request = buildLoginRequestDTO();
        LoginResponseDTO response = buildLoginResponseDTO();
        when(authService.loginUser(any(LoginRequestDTO.class))).thenReturn(response);
        String json = new ObjectMapper().writeValueAsString(request);

        // Act
        MvcResult mvcResult = mockMvc.perform(buildPostRequest(BASE_URL + "/login").content(json)).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        LoginResponseDTO loggedUser = new ObjectMapper().readValue(responseBodyAsString, LoginResponseDTO.class);

        // Assert
        assertNotNull(loggedUser.getToken());
        assertEquals(response.getToken(), loggedUser.getToken());
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());

        // Verify
        verify(authService, times(1)).loginUser(any(LoginRequestDTO.class));
    }

    @Order(5)
    @Test
    @DisplayName("Login without username")
    void testLoginUser_withoutUsername_returnsBadRequest() throws Exception {
        // Arrange
        LoginRequestDTO request = buildLoginRequestDTO();
        request.setUsername(" ");
        String json = new ObjectMapper().writeValueAsString(request);

        // Act
        MvcResult mvcResult = mockMvc.perform(buildPostRequest(BASE_URL + "/login").content(json)).andReturn();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());

        // Verify
        verify(authService, never()).loginUser(any(LoginRequestDTO.class));
    }

    @Order(6)
    @Test
    @DisplayName("Login without password")
    void testLoginUser_withoutPassword_returnsBadRequest() throws Exception {
        // Arrange
        LoginRequestDTO request = buildLoginRequestDTO();
        request.setPassword(" ");
        String json = new ObjectMapper().writeValueAsString(request);

        // Act
        MvcResult mvcResult = mockMvc.perform(buildPostRequest(BASE_URL + "/login").content(json)).andReturn();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());

        // Verify
        verify(authService, never()).loginUser(any(LoginRequestDTO.class));
    }

    @Order(7)
    @Test
    @DisplayName("Logout user with valid token")
    void testLogout_whenTokenIsValid_returnCorrectStatus() throws Exception {
        // Arrange
        String authHeader = "Bearer %s".formatted(TEST_TOKEN);
        when(authService.logout(any(HttpServletRequest.class))).thenReturn(ResponseEntity.ok("Logout successful"));

        // Act
        MvcResult mvcResult = mockMvc.perform(buildPostRequest(BASE_URL + "/logout").header("Authorization", authHeader)).andReturn();

        // Assert
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        assertEquals("Logout successful", mvcResult.getResponse().getContentAsString());

        // Verify
        verify(authService, times(1)).logout(any(HttpServletRequest.class));
    }

    @Order(8)
    @Test
    @DisplayName("Logout fails when Authorization header is missing")
    void testLogout_whenAuthorizationHeaderIsMissing_returnsBadRequest() throws Exception {
        // Arrange
        when(authService.logout(any(HttpServletRequest.class)))
                .thenReturn(ResponseEntity.badRequest().body("Missing or invalid Authorization header"));

        // Act
        MvcResult mvcResult = mockMvc.perform(buildPostRequest(BASE_URL + "/logout")).andReturn();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
        assertEquals("Missing or invalid Authorization header", mvcResult.getResponse().getContentAsString());

        // Verify
        verify(authService, times(1)).logout(any(HttpServletRequest.class));
    }
}