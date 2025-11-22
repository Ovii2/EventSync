package org.example.backend.service;

import org.example.backend.dto.user.UserRequestDTO;
import org.example.backend.dto.user.UserResponseDTO;
import org.example.backend.enums.UserRole;
import org.example.backend.exception.AlreadyExistsException;
import org.example.backend.exception.NotFoundException;
import org.example.backend.mapper.UserMapper;
import org.example.backend.model.User;
import org.example.backend.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private Argon2PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private UserDetails userDetails;

    private static final String TEST_USERNAME = "Test";
    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_PASSWORD = "12345678";


    UserRequestDTO validUserRequest() {
        return UserRequestDTO.builder()
                .username(TEST_USERNAME)
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();
    }

    UserResponseDTO validUserResponse() {
        return UserResponseDTO.builder()
                .id(UUID.randomUUID())
                .username(TEST_USERNAME)
                .email(TEST_EMAIL)
                .role(UserRole.ROLE_USER)
                .build();
    }

    User validUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .username(TEST_USERNAME)
                .email(TEST_EMAIL)
                .password("12345678")
                .role(UserRole.ROLE_USER)
                .build();
    }

    @Order(1)
    @Test
    @DisplayName("Create user with valid details")
    void testCreateUser_whenValidDetailsProvided_returnsUserResponseDTO() {
        // Arrange
        UserRequestDTO testUser = validUserRequest();
        User savedUser = validUser();

        when(userMapper.toEntity(testUser)).thenReturn(savedUser);
        when(passwordEncoder.encode(testUser.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(validUser());
        when(userMapper.toResponse(savedUser)).thenReturn(UserResponseDTO.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build());

        // Act
        UserResponseDTO user = userService.createUser(testUser);

        // Assert
        assertNotNull(testUser);
        assertNotNull(user.getId(), "Id is missing");
        assertEquals(testUser.getUsername(), user.getUsername());
        assertEquals(testUser.getEmail(), user.getEmail());

        // Verify
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Order(2)
    @Test
    @DisplayName("Create user with existing email")
    void testCreateUser_whenEmailAlreadyExists_throwsAlreadyExistsException() {
        // Arrange
        UserRequestDTO testUser = validUserRequest();
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

        // Act
        AlreadyExistsException thrown = assertThrows(AlreadyExistsException.class, () -> userService.createUser(testUser));

        // Assert
        assertEquals("Email already exists", thrown.getMessage());

        // Verify
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toEntity(any(UserRequestDTO.class));
        verify(userRepository, times(1)).existsByEmail(testUser.getEmail());
        verify(passwordEncoder, never()).encode(testUser.getPassword());
    }

    @Order(2)
    @Test
    @DisplayName("Create user with existing username")
    void testCreateUser_whenUsernameAlreadyExists_throwsAlreadyExistsException() {
        // Arrange
        UserRequestDTO testUser = validUserRequest();
        when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(true);

        // Act
        AlreadyExistsException thrown = assertThrows(AlreadyExistsException.class, () -> userService.createUser(testUser));

        // Assert
        assertEquals("Username already exists", thrown.getMessage());

        // Verify
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toEntity(any(UserRequestDTO.class));
        verify(userRepository, times(1)).existsByUsername(testUser.getUsername());
        verify(passwordEncoder, never()).encode(testUser.getPassword());
    }

    @Order(3)
    @Test
    @DisplayName("Get existing current user")
    void testGetCurrentUser_whenUserExists_returnsUser() {
        // Arrange
        User user = validUser();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        when(userDetails.getUsername()).thenReturn(TEST_USERNAME);
        when(userRepository.findUserByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));

        // Act
        var result = userService.getCurrentUser();

        // Assert
        assertTrue(result.isPresent());
        assertEquals(TEST_USERNAME, result.get().getUsername());
        assertEquals(TEST_EMAIL, result.get().getEmail());

        // Verify
        verify(userRepository, times(1)).findUserByUsername(TEST_USERNAME);
    }

    @Order(4)
    @Test
    @DisplayName("Get not existing current user")
    void testGetCurrentUser_whenUserDoesNotExist_returnEmptyUser() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        when(userDetails.getUsername()).thenReturn(TEST_USERNAME);
        when(userRepository.findUserByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        // Act
        var result = userService.getCurrentUser();

        // Assert
        assertTrue(result.isEmpty());

        // Verify
        verify(userRepository, times(1)).findUserByUsername(TEST_USERNAME);
    }

    @Order(5)
    @Test
    @DisplayName("User exists")
    void testCheckIfUserExists_whenUserExists_returnsUser() {
        // Arrange
        User user = validUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Act
        User existingUser = userService.checkIfUserExists(user.getId());

        // Assert
        assertNotNull(user.getId());
        assertEquals(user.getId(), existingUser.getId());

        // Verify
        verify(userRepository, times(1)).findById(existingUser.getId());
    }

    @Order(6)
    @Test
    @DisplayName("User does not exist")
    void testCheckIfUserExists_whenUserDoesNotExists_throwNotFoundException() {
        // Arrange
        User user = validUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        // Act
        var thrown = assertThrows(NotFoundException.class,
                () -> userService.checkIfUserExists(user.getId()));

        // Assert
        assertEquals("User was not found", thrown.getMessage());

        // Verify
        verify(userRepository, times(1)).findById(user.getId());
    }
}