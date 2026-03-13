package com.example.FairShare.dto.expenseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExpenseResponseDTO(
        Long expenseId,
        String description,
        BigDecimal amount,
        Long paidUserId,
        Long groupId,
        LocalDate expenseDate,
        LocalDateTime createdAt
) {
}
