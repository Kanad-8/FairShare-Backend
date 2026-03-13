package com.example.FairShare.service.strategy;

import com.example.FairShare.dto.splitDTO.SplitRequestDTO;
import com.example.FairShare.model.Expense;
import com.example.FairShare.model.Split;
import com.example.FairShare.model.User;
import com.example.FairShare.model.enums.SplitType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExactSplitStrategy implements SplitStrategy{
    @Override
    public SplitType getSplitType() {
        return SplitType.EXACT;
    }

    @Override
    public List<Split> calculateSplit(Expense expense, List<SplitRequestDTO> splitRequestList, Map<Long, User> groupUser) {
        if (splitRequestList == null || splitRequestList.isEmpty()) {
            throw new IllegalArgumentException("Split list cannot be empty");
        }

        BigDecimal total = BigDecimal.ZERO;
        List<Split> splits = new ArrayList<>();

        for (SplitRequestDTO splitDTO : splitRequestList) {
            total = total.add(splitDTO.amount());

            Split split = new Split();
            split.setAmount(splitDTO.amount());
            split.setUser(groupUser.get(splitDTO.userId()));
            split.setExpense(expense);

            splits.add(split);
        }

        if(total.compareTo(expense.getAmount()) != 0){
            throw new IllegalArgumentException("Amounts don't add up to total amount");
        }

        return splits;
    }
}
