package org.example.backend.service;

import org.example.backend.dto.login.LoginRequestDTO;
import org.example.backend.dto.login.LoginResponseDTO;
import org.example.backend.enums.TokenType;
import org.example.backend.enums.UserRole;
import org.example.backend.exception.UserAlreadyLoggedInException;
import org.example.backend.model.Token;
import org.example.backend.model.User;
import org.example.backend.repository.TokenRepository;
import org.example.backend.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenService tokenService;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private Authentication authentication;

    private static final String TEST_TOKEN = "valid-token";
    private static final String TEST_USERNAME = "user";
    private static final String TEST_EMAIL = "email@email.com";
    private static final String TEST_PASSWORD = "12345678";
    private static final UUID TEST_USERID = UUID.randomUUID();


    LoginResponseDTO setupLoginResponse() {
        return LoginResponseDTO.builder()
                .token(TEST_TOKEN)
                .build();
    }

    LoginRequestDTO setupLoginRequest() {
        return LoginRequestDTO.builder()
                .username(TEST_USERNAME)
                .password(TEST_PASSWORD)
                .build();
    }

    User setupUser() {
        return User.builder()
                .id(TEST_USERID)
                .username(TEST_USERNAME)
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .role(UserRole.ROLE_USER)
                .build();
    }


    @Order(1)
    @Test
    @DisplayName("User can login in")
    void testLoginUser_whenUserExists_returnsTokenAndLoginResponse() {
        // Arrange
        LoginRequestDTO request = setupLoginRequest();
        User user = setupUser();

        when(userRepository.findUserByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(tokenService.getAllValidUserTokens(user)).thenReturn(Collections.emptyList());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        doNothing().when(tokenService).deleteAllUserTokens(user);
        when(jwtService.generateToken(user)).thenReturn(TEST_TOKEN);
        doNothing().when(tokenService).saveUserToken(user, TEST_TOKEN);

        // Act
        var loginResponse = authService.loginUser(request);

        // Assert
        assertNotNull(loginResponse);
        assertEquals(TEST_TOKEN, loginResponse.getToken());

        // Verify
        verify(userRepository, times(1)).findUserByUsername(TEST_USERNAME);
        verify(jwtService, times(1)).generateToken(any(User.class));
        verify(tokenService).deleteAllUserTokens(user);
        verify(tokenService).saveUserToken(user, TEST_TOKEN);
    }

    @Order(2)
    @Test
    @DisplayName("User can't login, user doesn't exist")
    void testLoginUser_whenUserDoesNotExist_throwsUsernameNotFoundException() {
        // Arrange
        LoginRequestDTO notFoundRequest = LoginRequestDTO.builder()
                .username("not-found")
                .password("not-found")
                .build();

        when(userRepository.findUserByUsername(notFoundRequest.getUsername())).thenReturn(Optional.empty());

        // Act
        var thrown = assertThrows(UsernameNotFoundException.class, () -> authService.loginUser(notFoundRequest));

        // Assert
        assertNotNull(thrown);

        // Verify
        verify(userRepository, times(1)).findUserByUsername(notFoundRequest.getUsername());
        verifyNoInteractions(authenticationManager, tokenService, jwtService);
    }

    @Order(3)
    @Test
    @DisplayName("User can't login twice in a row")
    void testLoginUser_whenUserIsAlreadyLoggedIn_throwsUserAlreadyLoggedInException() {
        // Arrange
        LoginRequestDTO request = setupLoginRequest();
        User user = setupUser();
        Token token = Token.builder()
                .id(UUID.randomUUID())
                .token("token")
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        List<Token> tokens = List.of(token);

        when(userRepository.findUserByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(tokenService.getAllValidUserTokens(user)).thenReturn(tokens);

        // Act
        var thrown = assertThrows(UserAlreadyLoggedInException.class, () -> authService.loginUser(request));

        // Assert
        assertNotNull(thrown);

        // Verify
        verify(tokenService, times(1)).getAllValidUserTokens(user);
        verify(userRepository, times(1)).findUserByUsername(request.getUsername());
        verifyNoInteractions(authenticationManager, jwtService);
        verifyNoMoreInteractions(tokenService);
    }
}
