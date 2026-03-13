package com.example.FairShare.service.strategy;

import com.example.FairShare.dto.splitDTO.SplitRequestDTO;
import com.example.FairShare.model.Expense;
import com.example.FairShare.model.Split;
import com.example.FairShare.model.User;
import com.example.FairShare.model.enums.SplitType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class EqualSplitStrategy implements SplitStrategy{

    @Override
    public SplitType getSplitType() {
        return SplitType.EQUAL;
    }

    @Override
    public List<Split> calculateSplit(Expense expense, List<SplitRequestDTO> splitRequestList, Map<Long, User> groupUser) {
        if (splitRequestList == null || splitRequestList.isEmpty()) {
            throw new IllegalArgumentException("Split list cannot be empty");
        }

        BigDecimal perSplit = expense.getAmount().divide(BigDecimal.valueOf(splitRequestList.size()),2, RoundingMode.DOWN);
        BigDecimal leftPenny = expense.getAmount().subtract(perSplit.multiply(BigDecimal.valueOf(splitRequestList.size())));

        List<Split> splits = new ArrayList<>();

        for(SplitRequestDTO splitDTO:splitRequestList){
            Split split = new Split();
            split.setExpense(expense);
            split.setAmount(perSplit);
            split.setUser(groupUser.get(splitDTO.userId()));
            splits.add(split);
        }

        splits.getFirst().setAmount(perSplit.add(leftPenny));

        return splits;
    }

}
