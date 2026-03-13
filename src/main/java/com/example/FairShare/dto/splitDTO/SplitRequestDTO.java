package com.example.FairShare.dto.splitDTO;

import com.example.FairShare.model.User;
import com.example.FairShare.model.enums.SplitType;

import java.math.BigDecimal;

public record SplitRequestDTO (
    Long userId,
    BigDecimal amount,
    BigDecimal percentage
){}
