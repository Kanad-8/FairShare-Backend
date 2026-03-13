package com.example.FairShare.service.strategy;

import com.example.FairShare.dto.splitDTO.SplitRequestDTO;
import com.example.FairShare.model.Expense;
import com.example.FairShare.model.Split;
import com.example.FairShare.model.User;
import com.example.FairShare.model.enums.SplitType;

import java.util.List;
import java.util.Map;

public interface SplitStrategy {

    SplitType getSplitType();

    List<Split> calculateSplit(Expense expense, List<SplitRequestDTO> splitRequestList, Map<Long, User> groupUser);
}
