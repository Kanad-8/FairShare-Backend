package com.example.FairShare.dto.groupDTO;

import com.example.FairShare.dto.userDTO.UserResponseDTO;

import java.util.List;

public record GroupRequestDTO(
        String name,
        List<Long> member
) {
}
