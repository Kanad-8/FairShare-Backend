package com.example.FairShare.repository;

import com.example.FairShare.dto.commonDTO.BalanceDTO;
import com.example.FairShare.model.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement,Long> {

    // All settlements paid by the user to others in the group (Decreases their debt)
    @Query("""
    SELECT new com.example.FairShare.dto.commonDTO.BalanceDTO(
        s.paidByUser.userId, COALESCE(SUM(s.amount), 0)
    )
    FROM Settlement s
    WHERE s.group.groupId = :groupId
    GROUP BY s.paidByUser.userId
    """)
    List<BalanceDTO> getSettlementsPaid(@Param("groupId") Long groupId);

    // All settlements received by the user from others in the group (Increases their debt)
    @Query("""
    SELECT new com.example.FairShare.dto.commonDTO.BalanceDTO(
        s.paidToUser.userId, COALESCE(SUM(s.amount), 0)
    )
    FROM Settlement s
    WHERE s.group.groupId = :groupId
    GROUP BY s.paidToUser.userId
    """)
    List<BalanceDTO> getSettlementsReceived(@Param("groupId") Long groupId);

    List<Settlement> findByGroupGroupId(Long groupId);

}
