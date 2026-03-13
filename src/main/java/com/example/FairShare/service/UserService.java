package com.example.FairShare.service;

import com.example.FairShare.dto.userDTO.UserRequestDTO;
import com.example.FairShare.dto.userDTO.UserResponseDTO;
import com.example.FairShare.model.User;
import com.example.FairShare.repository.UserRepository;
import com.example.FairShare.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Security;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
//    @Transactional
//    public UserResponseDTO createUser(UserRequestDTO request) {
//        // 1. Validate uniqueness
//        if (userRepository.existsByEmail(request.email())) {
//            throw new IllegalArgumentException("Email is already in use.");
//        }
//        if (userRepository.existsByUsername(request.username())) {
//            throw new IllegalArgumentException("Username is already taken.");
//        }
//
//        // 2. Map and Save
//        User user = new User();
//        user.setUsername(request.username());
//        user.setEmail(request.email());
//        user.setName(request.name());
//        // Note: Password hashing would happen here eventually!
//
//        User savedUser = userRepository.save(user);
//        return mapToResponseDTO(savedUser);
//    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById() {
        Long id = securityUtils.getCurrentUserId();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return mapToResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getUsersByGroupId(Long groupId) {
        return userRepository.findByGroupGroupId(groupId).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Transactional
    public UserResponseDTO updateUser(UserRequestDTO request) {
        Long id = securityUtils.getCurrentUserId();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        // Only check for duplicate email if they are actually changing it
        if (!user.getEmail().equals(request.email()) && userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already in use by another account.");
        }

        user.setUsername(request.username());
        user.setEmail(request.email());

        User updatedUser = userRepository.save(user);
        return mapToResponseDTO(updatedUser);
    }

    @Transactional
    public void deleteUser() {
        Long id = securityUtils.getCurrentUserId();
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        return new UserResponseDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}