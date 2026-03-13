package com.example.FairShare.dto.settlementDTO;

import java.math.BigDecimal;

public record SettlementRequestDTO(
        Long groupId,
        BigDecimal amount,
        Long paidByUserId,
        Long paidToUserId
) {
}
