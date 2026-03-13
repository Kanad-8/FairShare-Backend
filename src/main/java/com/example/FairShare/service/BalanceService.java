package com.example.FairShare.service;

import com.example.FairShare.dto.commonDTO.BalanceDTO;
import com.example.FairShare.model.Group;
import com.example.FairShare.model.User;
import com.example.FairShare.repository.ExpenseRepository;
import com.example.FairShare.repository.GroupRepository;
import com.example.FairShare.repository.SplitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class BalanceService {

    private final ExpenseRepository expenseRepository;
    private final SplitRepository splitRepository;
    private final GroupRepository groupRepository;

    @Transactional(readOnly = true)
    public Map<Long, BigDecimal> getLedger(Long groupId) {
        Map<Long, BigDecimal> ledger = new HashMap<>();

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group does not exist"));

        for (User member : group.getMembers()) {
            ledger.put(member.getUserId(), BigDecimal.ZERO);
        }


        List<BalanceDTO> credits = expenseRepository.getGroupCredit(groupId);
        for (BalanceDTO dto : credits) {
            if (ledger.containsKey(dto.userId())) {
                ledger.computeIfPresent(dto.userId(), (k, currentBalance) -> currentBalance.add(dto.amount()));
            }
        }

        // 3. Subtract Debits (Money they owe -> Balance goes DOWN)
        List<BalanceDTO> debits = splitRepository.getGroupDebit(groupId);
        for (BalanceDTO dto : debits) {
            if (ledger.containsKey(dto.userId())) {
                ledger.computeIfPresent(dto.userId(), (k, currentBalance) -> currentBalance.subtract(dto.amount()));
            }
        }

        return ledger;
    }

}
