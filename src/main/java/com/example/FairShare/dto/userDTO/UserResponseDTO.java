package com.example.FairShare.dto.userDTO;

import java.time.LocalDateTime;

public record UserResponseDTO(
        Long userId,
        String email,
        String username,
        LocalDateTime createdAt
) {
}
