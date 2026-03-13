package com.example.FairShare.service;

import com.example.FairShare.dto.expenseDTO.ExpenseRequestDTO;
import com.example.FairShare.dto.expenseDTO.ExpenseResponseDTO;
import com.example.FairShare.model.Expense;
import com.example.FairShare.model.Group;
import com.example.FairShare.model.Split;
import com.example.FairShare.model.User;
import com.example.FairShare.repository.ExpenseRepository;
import com.example.FairShare.repository.GroupRepository;
import com.example.FairShare.repository.UserRepository;
import com.example.FairShare.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final SplitService splitService;
    private final SecurityUtils securityUtils;

    @Transactional
    public ExpenseResponseDTO createExpense(ExpenseRequestDTO request) {

        Long loggedInUserId = securityUtils.getCurrentUserId();
        User loggedInUser = userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new EntityNotFoundException("Authenticated user not found"));

        Group group = groupRepository.findById(request.groupId())
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + request.groupId()));

        if (!group.getMembers().contains(loggedInUser)) {
            throw new SecurityException("You must be a member of this group to log an expense here.");
        }

        User paidBy = userRepository.findById(request.paidUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.paidUserId()));

        if (!group.getMembers().contains(paidBy)) {
            throw new IllegalArgumentException("The user paying the expense must be a member of the group.");
        }

        Expense expense = new Expense();
        expense.setDescription(request.description());
        expense.setAmount(request.amount());
        expense.setExpenseDate(request.expenseDate());
        expense.setPaidUser(paidBy); // We set the actual payer, not necessarily the logger!
        expense.setGroup(group);

        Expense savedExpense = expenseRepository.save(expense);

        List<Split> splits = splitService.generateSplit(savedExpense, request);
        for(Split split : splits){
            savedExpense.addSplit(split);
        }
        savedExpense = expenseRepository.save(savedExpense);

        return mapToResponseDTO(savedExpense);
    }

    @Transactional(readOnly = true)
    public ExpenseResponseDTO getExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found with id: " + id));

        return mapToResponseDTO(expense);
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponseDTO> getExpensesByGroupId(Long groupId) {
        // Assumes you have List<Expense> findByGroup_Id(Long groupId) in ExpenseRepository
        return expenseRepository.findByGroupGroupId(groupId).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Transactional
    public ExpenseResponseDTO updateExpense(Long id, ExpenseRequestDTO request) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found with id: " + id));

        // Update basic fields
        expense.setDescription(request.description());
        expense.setAmount(request.amount());
        expense.setExpenseDate(request.expenseDate());

        // If the payer changed, fetch the new user
        if (!expense.getPaidUser().getUserId().equals(request.paidUserId())) {
            User newPayer = userRepository.findById(request.paidUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.paidUserId()));

            // Validate the new payer is in the group
            if (!expense.getGroup().getMembers().contains(newPayer)) {
                throw new IllegalArgumentException("The new paying user must be a member of the group.");
            }
            expense.setPaidUser(newPayer);
        }

        Expense updatedExpense = expenseRepository.save(expense);
        return mapToResponseDTO(updatedExpense);
    }

    @Transactional
    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new EntityNotFoundException("Expense not found with id: " + id);
        }
        expenseRepository.deleteById(id);
    }

    private ExpenseResponseDTO mapToResponseDTO(Expense expense) {
        return new ExpenseResponseDTO(
                expense.getExpenseId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getPaidUser().getUserId(),
                expense.getGroup().getGroupId(),
                expense.getExpenseDate(),
                expense.getCreatedAt()
        );
    }
}
