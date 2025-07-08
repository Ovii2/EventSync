package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.user.UserRequestDTO;
import org.example.backend.dto.user.UserResponseDTO;
import org.example.backend.exception.UserAlreadyExistsException;
import org.example.backend.mapper.UserMapper;
import org.example.backend.model.User;
import org.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        if (userRepository.existsByUsername(userRequestDTO.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        User user = userMapper.toEntity(userRequestDTO);
        userRepository.save(user);
        return userMapper.toResponse(user);
    }
}
