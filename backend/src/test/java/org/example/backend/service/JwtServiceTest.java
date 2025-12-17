package org.example.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.example.backend.enums.UserRole;
import org.example.backend.model.User;
import org.example.backend.userDetails.CustomUserDetails;
import org.junit.jupiter.api.*;

import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JwtServiceTest {

    private JwtService jwtService;

    public static final String BASE64_KEY = "XJz8K7mQ9vR2nL4pY6tH3wE1sA5fD8gB0cN7jU9iO2k=";
    private static final String TEST_USERNAME = "Test";
    private static final String TEST_EMAIL = "test@email.com";

    User setupUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .username(TEST_USERNAME)
                .email(TEST_EMAIL)
                .password("12345678")
                .role(UserRole.ROLE_USER)
                .build();
    }

    User setupInvalidUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .username("invalid-username")
                .email("invalid-email")
                .password("12345678")
                .role(UserRole.ROLE_USER)
                .build();
    }

    @BeforeEach
    void setup() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 60_000L);
        ReflectionTestUtils.setField(jwtService, "secretKey", BASE64_KEY);
    }

    @Test
    @DisplayName("Return correct username")
    void testExtractUsername_whenUsernameExists_returnUsername() {
        // Arrange
        User user = setupUser();
        String token = jwtService.generateToken(user);

        // Act
        String username = jwtService.extractUsername(token);

        // Assert
        assertFalse(jwtService.isTokenExpired(token));
        assertTrue(jwtService.isTokenValid(token, new CustomUserDetails(user)));
        assertEquals(TEST_USERNAME, username);
    }

    @Test
    @DisplayName("Throws exception for malformed token")
    void testExtractUsername_whenTokenIsMalformed_throwsJwtException() {
        // Arrange
        String malformedToken = "not-a-jwt";

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                jwtService.extractUsername(malformedToken)
        );
    }

    @Test
    @DisplayName("Returns token, when valid user exists")
    void testGenerateToken_whenUserExists_returnsToken() {
        // Arrange
        User user = setupUser();

        // Act
        String token = jwtService.generateToken(user);

        // Assert
        assertNotNull(token);
        assertEquals(TEST_USERNAME, jwtService.extractUsername(token));
        assertFalse(jwtService.isTokenExpired(token));
    }

    @Test
    @DisplayName("Builds token with custom claims and expiration")
    void testBuildToken_whenCustomClaimsProvided_returnsValidToken() {
        // Arrange
        User user = setupUser();
        Map<String, Object> customClaims = new HashMap<>();
        customClaims.put("customKey", "customValue");
        CustomUserDetails userDetails = new CustomUserDetails(user);

        // Act
        String token = jwtService.buildToken(customClaims, userDetails, 60_000L);

        // Assert
        assertNotNull(token);
        Claims claims = jwtService.extractAllClaims(token);
        assertEquals("customValue", claims.get("customKey", String.class));
        assertEquals(TEST_USERNAME, claims.getSubject());
    }

    @Test
    @DisplayName("Token is valid when username matches")
    void testIsTokenValid_whenUsernameMatches_returnsTrue() {
        // Arrange
        User user = setupUser();
        String token = jwtService.generateToken(user);

        // Act
        boolean valid = jwtService.isTokenValid(token, new CustomUserDetails(user));

        // Assert
        assertTrue(valid);
        assertEquals(TEST_USERNAME, jwtService.extractUsername(token));
        assertFalse(jwtService.isTokenExpired(token));
    }

    @DisplayName("Token is invalid, when user does not match")
    @Test
    void testIsTokenValid_whenUsernamesDoesNotMatch_returnsFalse() {
        // Arrange
        User user = setupUser();
        String token = jwtService.generateToken(user);

        User otherUser = setupInvalidUser();

        // Act
        boolean valid = jwtService.isTokenValid(token, new CustomUserDetails(otherUser));

        // Assert
        assertFalse(valid);
    }

    @Test
    @DisplayName("Returns false, token is not expired")
    void testIsTokenExpired_whenTokenIsNotExpired_returnsFalse() {
        // Arrange
        User user = setupUser();
        String token = jwtService.generateToken(user);

        // Act
        boolean isExpired = jwtService.isTokenExpired(token);

        // Assert
        assertFalse(isExpired);
    }

    @Test
    @DisplayName("Throws exception when token is expired")
    void testIsTokenExpired_whenTokenIsExpired_throwsExpiredJwtException() {
        // Arrange
        User user = setupUser();
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L);
        String expiredToken = jwtService.generateToken(user);

        // Act & Assert
        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenExpired(expiredToken));
    }

    @Test
    @DisplayName("Returns future expiration date for valid token")
    void testExtractExpiration_whenTokenIsValid_returnsFutureDate() {
        // Arrange
        User user = setupUser();
        String token = jwtService.generateToken(user);

        // Act
        Date expirationDate = jwtService.extractExpiration(token);

        // Assert
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    @DisplayName("Extracts username claim from token")
    void testExtractClaim_whenUsernameClaimExists_returnsUsername() {
        // Arrange
        User user = setupUser();
        String token = jwtService.generateToken(user);

        // Act
        String claim = jwtService.extractClaim(token, Claims::getSubject);

        // Assert
        assertEquals(TEST_USERNAME, claim);
    }

    @Test
    @DisplayName("Extracts expiration date from token")
    void testExtractExpiration_whenCalled_returnsExpirationDate() {
        // Arrange
        User user = setupUser();
        String token = jwtService.generateToken(user);

        // Act
        Date expiration = jwtService.extractExpiration(token);

        // Assert
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    @DisplayName("Extracts all custom claims from valid token")
    void testExtractAllClaims_whenTokenIsValid_returnsExpectedClaims() {
        // Arrange
        User user = setupUser();
        String token = jwtService.generateToken(user);

        // Act
        Claims claims = jwtService.extractAllClaims(token);

        // Assert
        assertEquals(TEST_USERNAME, claims.get("username", String.class));
        assertEquals(TEST_USERNAME, claims.get("username", String.class));
        assertEquals(user.getId().toString(), claims.get("id", String.class));
        assertEquals(UserRole.ROLE_USER.name(), claims.get("role", String.class));
    }

    @Test
    @DisplayName("Creates signing key from valid secret")
    void testGetSignInKey_whenSecretKeyIsValid_returnsNonNullKey() {
        // Act
        Key signInKey = jwtService.getSignInKey();

        // Assert
        assertNotNull(signInKey);
    }
}