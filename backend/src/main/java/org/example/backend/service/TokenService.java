package org.example.backend.service;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.dto.websocket.WebSocketMessageDTO;
import org.example.backend.enums.TokenType;
import org.example.backend.model.Token;
import org.example.backend.model.User;
import org.example.backend.repository.TokenRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final SimpMessagingTemplate messagingTemplate;

    public void saveUserToken(User user, String jwtToken) {
        if (user == null || jwtToken == null) {
            throw new IllegalArgumentException("User and JWT token must not be null");
        }

        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllByUserIdAndExpiredIsFalseAndRevokedIsFalse(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void deleteAllUserTokens(User user) {
        var allUserTokens = tokenRepository.findAllByUserId(user.getId());
        if (!allUserTokens.isEmpty()) {
            tokenRepository.deleteAll(allUserTokens);
        }
    }

    public List<Token> getAllValidUserTokens(User user) {
        return tokenRepository.findAllByUserIdAndExpiredIsFalseAndRevokedIsFalse(user.getId());
    }

    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredTokens() {
        List<Token> allTokens = tokenRepository.findAll();

        List<Token> expiredTokens = allTokens.stream()
                .filter(token -> {
                    try {
                        return jwtService.isTokenExpired(token.getToken());
                    } catch (ExpiredJwtException e) {
                        log.info("Token expired: id={}, expiresAt={}", token.getId(), e.getClaims().getExpiration());
                        return true;
                    } catch (Exception e) {
                        log.warn("Failed to check token {}: {}", token.getToken(), e.getMessage());
                        return false;
                    }
                })
                .toList();

        expiredTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);

            WebSocketMessageDTO message = WebSocketMessageDTO.builder()
                    .type("SESSION_EXPIRED")
                    .data("Your session has expired. Please log in again.")
                    .build();

            String username = token.getUser().getUsername();
            messagingTemplate.convertAndSendToUser(username, "/queue/session", message);
            log.info("Sent session expiration message to user: {}", username);
        });

        if (!expiredTokens.isEmpty()) {
            tokenRepository.deleteAll(expiredTokens);
        }
    }
}
