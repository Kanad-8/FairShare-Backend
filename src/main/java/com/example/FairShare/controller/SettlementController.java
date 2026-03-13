package com.example.FairShare.controller;

import com.example.FairShare.dto.settlementDTO.SettlementRequestDTO;
import com.example.FairShare.dto.settlementDTO.SettlementResponseDTO;
import com.example.FairShare.service.SettlementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    // 1. Record an actual physical payment between two users
    @PostMapping
    public ResponseEntity<SettlementResponseDTO> createSettlement(@Valid @RequestBody SettlementRequestDTO request) {
        SettlementResponseDTO response = settlementService.createSettlement(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. Get details of a specific recorded settlement
    @GetMapping("/{id}")
    public ResponseEntity<SettlementResponseDTO> getSettlementById(@PathVariable Long id) {
        SettlementResponseDTO response = settlementService.getSettlementById(id);
        return ResponseEntity.ok(response);
    }

    // 3. Get history of all recorded settlements in a group
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<SettlementResponseDTO>> getSettlementsByGroupId(@PathVariable Long groupId) {
        List<SettlementResponseDTO> responses = settlementService.getSettlementsByGroupId(groupId);
        return ResponseEntity.ok(responses);
    }

    // 4. Update an existing settlement (e.g., if they typed $50 instead of $40)
    @PutMapping("/{id}")
    public ResponseEntity<SettlementResponseDTO> updateSettlement(
            @PathVariable Long id,
            @Valid @RequestBody SettlementRequestDTO request) {
        SettlementResponseDTO response = settlementService.updateSettlement(id, request);
        return ResponseEntity.ok(response);
    }

    // 5. Delete a settlement record
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSettlement(@PathVariable Long id) {
        settlementService.deleteSettlement(id);
        return ResponseEntity.noContent().build();
    }

    // 6. THE CROWN JEWEL: Run the Greedy Algorithm to get minimum cash flow
    @GetMapping("/group/{groupId}/suggested")
    public ResponseEntity<List<SettlementRequestDTO>> getSuggestedSettlements(@PathVariable Long groupId) {
        List<SettlementRequestDTO> responses = settlementService.getSuggestedSettlements(groupId);
        return ResponseEntity.ok(responses);
    }
}