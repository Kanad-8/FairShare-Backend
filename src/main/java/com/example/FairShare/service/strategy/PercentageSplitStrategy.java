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

public class PercentageSplitStrategy implements SplitStrategy{
    @Override
    public SplitType getSplitType() {
        return SplitType.PERCENTAGE;
    }

    @Override
    public List<Split> calculateSplit(Expense expense, List<SplitRequestDTO> splitRequestList, Map<Long, User> groupUser) {
        if (splitRequestList == null || splitRequestList.isEmpty()) {
            throw new IllegalArgumentException("Split list cannot be empty");
        }

        List<Split> splits = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalPercentage = BigDecimal.ZERO;

        if (totalPercentage.compareTo(new BigDecimal("100")) != 0) {
            throw new IllegalArgumentException("Split percentages must add up to exactly 100");
        }

        for(SplitRequestDTO splitDTO :splitRequestList){
            BigDecimal amount = expense.getAmount().multiply(splitDTO.percentage()).divide(BigDecimal.valueOf(100),2, RoundingMode.DOWN);
            totalAmount = totalAmount.add(amount);
            Split split = new Split();
            split.setAmount(amount);
            split.setUser(groupUser.get(splitDTO.userId()));
            split.setExpense(expense);
            splits.add(split);
        }
        BigDecimal pennyLeft = expense.getAmount().subtract(totalAmount);

        if(pennyLeft.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("Split percentages don't add up to 100");
        }

        splits.getFirst().setAmount(splits.getFirst().getAmount().add(pennyLeft));
        return splits;
    }
}
