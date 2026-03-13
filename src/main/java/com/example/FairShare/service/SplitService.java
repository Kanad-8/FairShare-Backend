package com.example.FairShare.service;

import com.example.FairShare.dto.expenseDTO.ExpenseRequestDTO;
import com.example.FairShare.dto.splitDTO.SplitRequestDTO;
import com.example.FairShare.model.Expense;
import com.example.FairShare.model.Split;
import com.example.FairShare.model.User;
import com.example.FairShare.model.enums.SplitType;
import com.example.FairShare.repository.SplitRepository;
import com.example.FairShare.service.strategy.SplitStrategy;
import com.example.FairShare.service.strategy.SplitStrategyFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SplitService {

    private final SplitRepository splitRepository;

    private final SplitStrategyFactory splitStrategyFactory;

    @Transactional
    public List<Split> generateSplit(Expense expense, ExpenseRequestDTO expenseRequestDTO){

        SplitType splitType = expenseRequestDTO.splitType();
        SplitStrategy strategy = splitStrategyFactory.getStrategy(splitType);

        List<SplitRequestDTO> splits = expenseRequestDTO.splits();

        Map<Long,User> groupUsers = expense.getGroup().getMembers().stream().collect(Collectors.toMap(User::getUserId,user->user));

        for(SplitRequestDTO split:splits){
            if(!groupUsers.containsKey(split.userId())){
                throw new IllegalArgumentException("User " + split.userId() + " does not exist or is not part of the Group");
            }
        }
        return strategy.calculateSplit(expense,splits,groupUsers);
    }
}
