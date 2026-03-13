package com.example.FairShare.dto.groupDTO;

import java.time.LocalDateTime;
import java.util.List;

public record GroupResponseDTO(
        Long groupId,
        String name,
        List<Long> members,
        LocalDateTime createdAt
) {
}
