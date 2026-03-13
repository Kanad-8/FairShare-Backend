package com.example.FairShare.service;

import com.example.FairShare.dto.settlementDTO.SettlementRequestDTO;
import jakarta.transaction.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class DebtSimplificationService {

    List<SettlementRequestDTO> simplifyDebt(Map<Long, BigDecimal> users, Long groupId) {
        List<SettlementRequestDTO> transactions = new ArrayList<>();

        Comparator<Pair> comparator = (p1, p2) -> p2.amount.compareTo(p1.amount);
        PriorityQueue<Pair> debit = new PriorityQueue<>(comparator);
        PriorityQueue<Pair> credit = new PriorityQueue<>(comparator);

        for (Map.Entry<Long, BigDecimal> entry : users.entrySet()) {
            BigDecimal amt = entry.getValue();
            Long key = entry.getKey();

            if (amt.compareTo(BigDecimal.ZERO) > 0) {
                credit.add(new Pair(key, amt));
            } else if (amt.compareTo(BigDecimal.ZERO) < 0) {
                debit.add(new Pair(key, amt.abs()));
            }
        }


        while (!credit.isEmpty() && !debit.isEmpty()) {
            Pair creditPair = credit.poll(); // The person owed the most
            Pair debitPair = debit.poll();   // The person who owes the most

            BigDecimal settledAmount = creditPair.amount.min(debitPair.amount);

            SettlementRequestDTO trans = new SettlementRequestDTO(
                    groupId, settledAmount, debitPair.userId, creditPair.userId
            );
            transactions.add(trans);

            BigDecimal newCreditBalance = creditPair.amount.subtract(settledAmount);
            BigDecimal newDebitBalance = debitPair.amount.subtract(settledAmount);

            if (newCreditBalance.compareTo(BigDecimal.ZERO) > 0) {
                credit.add(new Pair(creditPair.userId, newCreditBalance));
            }
            if (newDebitBalance.compareTo(BigDecimal.ZERO) > 0) {
                debit.add(new Pair(debitPair.userId, newDebitBalance));
            }
        }

        return transactions;
    }
    static class Pair{
        Long userId;
        BigDecimal amount;

        public Pair(Long userId,BigDecimal amount){
            this.userId=userId;
            this.amount=amount;
        }
    }
}
