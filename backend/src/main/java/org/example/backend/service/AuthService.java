package org.example.backend.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.login.LoginRequestDTO;
import org.example.backend.dto.login.LoginResponseDTO;
import org.example.backend.exception.UserAlreadyLoggedInException;
import org.example.backend.model.User;
import org.example.backend.repository.TokenRepository;
import org.example.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final TokenRepository tokenRepository;

    public LoginResponseDTO loginUser(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findUserByUsername(loginRequestDTO.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!tokenService.getAllValidUserTokens(user).isEmpty()) {
            throw new UserAlreadyLoggedInException("You are already logged in");
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword()));

        tokenService.deleteAllUserTokens(user);

        String jwtToken = jwtService.generateToken(user);
        tokenService.saveUserToken(user, jwtToken);

        return LoginResponseDTO.builder()
                .token(jwtToken)
                .build();
    }

    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }

        String jwt = authHeader.substring(7);
        return tokenRepository.findByToken(jwt)
                .map(token -> {
                    token.setExpired(true);
                    token.setRevoked(true);
                    tokenRepository.delete(token);
                    return ResponseEntity.ok("Logout successful");
                })
                .orElseGet(() -> ResponseEntity.badRequest().body("Invalid JWT token"));
    }
}
