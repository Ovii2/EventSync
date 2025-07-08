package org.example.backend.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.backend.enums.TokenType;
import org.example.backend.model.Token;
import org.example.backend.model.User;
import org.example.backend.repository.TokenRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;
    private final HttpServletRequest request;

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
}
