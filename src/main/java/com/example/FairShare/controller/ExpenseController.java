package com.example.FairShare.controller;

import com.example.FairShare.dto.expenseDTO.ExpenseRequestDTO;
import com.example.FairShare.dto.expenseDTO.ExpenseResponseDTO;
import com.example.FairShare.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/expense")
@RestController
public class ExpenseController {
    private final ExpenseService expenseService;

    // 1. Create a new expense
    @PostMapping
    public ResponseEntity<ExpenseResponseDTO> createExpense(@Valid @RequestBody ExpenseRequestDTO request) {
        ExpenseResponseDTO response = expenseService.createExpense(request);
        // Returns a 201 CREATED status instead of a standard 200 OK
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. Get a specific expense by its ID
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponseDTO> getExpenseById(@PathVariable Long id) {
        ExpenseResponseDTO response = expenseService.getExpenseById(id);
        return ResponseEntity.ok(response);
    }

    // 3. Get all expenses for a specific group
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<ExpenseResponseDTO>> getExpensesByGroupId(@PathVariable Long groupId) {
        List<ExpenseResponseDTO> responses = expenseService.getExpensesByGroupId(groupId);
        return ResponseEntity.ok(responses);
    }

    // 4. Update an existing expense
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponseDTO> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequestDTO request) {

        ExpenseResponseDTO response = expenseService.updateExpense(id, request);
        return ResponseEntity.ok(response);
    }

    // 5. Delete an expense
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        // Returns a 204 NO CONTENT status (Standard practice for successful deletes)
        return ResponseEntity.noContent().build();
    }

}
