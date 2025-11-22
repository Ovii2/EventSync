package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.user.UserRequestDTO;
import org.example.backend.dto.user.UserResponseDTO;
import org.example.backend.enums.UserRole;
import org.example.backend.exception.AlreadyExistsException;
import org.example.backend.exception.NotFoundException;
import org.example.backend.mapper.UserMapper;
import org.example.backend.model.User;
import org.example.backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Argon2PasswordEncoder passwordEncoder;

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new AlreadyExistsException("Email already exists");
        }

        if (userRepository.existsByUsername(userRequestDTO.getUsername())) {
            throw new AlreadyExistsException("Username already exists");
        }

        User user = userMapper.toEntity(userRequestDTO);
        user.setRole(UserRole.ROLE_USER);
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    public Optional<User> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return userRepository.findUserByUsername(userDetails.getUsername());
        }
        return Optional.empty();
    }

    public User checkIfUserExists(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User was not found"));
    }
}
