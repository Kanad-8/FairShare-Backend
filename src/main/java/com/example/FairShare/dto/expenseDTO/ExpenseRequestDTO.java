package com.example.FairShare.dto.expenseDTO;

import com.example.FairShare.dto.splitDTO.SplitRequestDTO;
import com.example.FairShare.model.enums.SplitType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ExpenseRequestDTO(
        String description,
        BigDecimal amount,
        Long paidUserId,
        Long groupId,
        LocalDate expenseDate,
        SplitType splitType,
        List<SplitRequestDTO> splits
) {
}
