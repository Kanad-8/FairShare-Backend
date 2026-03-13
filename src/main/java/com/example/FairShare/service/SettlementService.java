package com.example.FairShare.service;

import com.example.FairShare.dto.settlementDTO.SettlementRequestDTO;
import com.example.FairShare.dto.settlementDTO.SettlementResponseDTO;
import com.example.FairShare.model.Group;
import com.example.FairShare.model.Settlement;
import com.example.FairShare.repository.GroupRepository;
import com.example.FairShare.repository.SettlementRepository;
import com.example.FairShare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.id.BulkInsertionCapableIdentifierGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import com.example.FairShare.model.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final BalanceService balanceService;
    private final DebtSimplificationService debtSimplificationService;

    @Transactional
    public SettlementResponseDTO createSettlement(SettlementRequestDTO request) {

        Group group = groupRepository.findById(request.groupId())
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + request.groupId()));

        User paidBy = userRepository.findById(request.paidByUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.paidByUserId()));

        User paidTo = userRepository.findById(request.paidToUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.paidToUserId()));

        if (!group.getMembers().contains(paidBy) || !group.getMembers().contains(paidTo)) {
            throw new IllegalArgumentException("Both users must be members of the group to settle debts.");
        }

        Settlement settlement = new Settlement();
        settlement.setGroup(group);
        settlement.setAmount(request.amount());
        settlement.setPaidByUser(paidBy);
        settlement.setPaidToUser(paidTo);

        Settlement savedSettlement = settlementRepository.save(settlement);
        return mapToResponseDTO(savedSettlement);
    }

    @Transactional(readOnly = true)
    public SettlementResponseDTO getSettlementById(Long id) {
        Settlement settlement = settlementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Settlement not found with id: " + id));
        return mapToResponseDTO(settlement);
    }


    @Transactional(readOnly = true)
    public List<SettlementResponseDTO> getSettlementsByGroupId(Long groupId) {
        return settlementRepository.findByGroupGroupId(groupId).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Transactional
    public SettlementResponseDTO updateSettlement(Long id, SettlementRequestDTO request) {
        Settlement settlement = settlementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Settlement not found with id: " + id));

        settlement.setAmount(request.amount());

        Settlement updatedSettlement = settlementRepository.save(settlement);
        return mapToResponseDTO(updatedSettlement);
    }

    @Transactional
    public void deleteSettlement(Long id) {
        if (!settlementRepository.existsById(id)) {
            throw new EntityNotFoundException("Settlement not found with id: " + id);
        }
        settlementRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<SettlementRequestDTO> getSuggestedSettlements(Long groupId){

        Map<Long, BigDecimal> ledger = balanceService.getLedger(groupId);

         if(ledger.isEmpty()){
             return new ArrayList<>();
         }
         return debtSimplificationService.simplifyDebt(ledger,groupId);

    }

    private SettlementResponseDTO mapToResponseDTO(Settlement settlement) {
        return new SettlementResponseDTO(
                settlement.getSettlementId(),
                settlement.getGroup().getGroupId(),
                settlement.getAmount(),
                settlement.getPaidByUser().getUserId(),
                settlement.getPaidToUser().getUserId()
        );
    }

}
