package com.example.FairShare.dto.commonDTO;

import java.math.BigDecimal;

public record BalanceDTO(
        Long userId,
        BigDecimal amount
) {
}
