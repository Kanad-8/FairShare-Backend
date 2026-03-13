package com.example.FairShare.repository;

import com.example.FairShare.dto.commonDTO.BalanceDTO;
import com.example.FairShare.model.Split;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SplitRepository extends JpaRepository<Split,Long> {

    List<Split> findByUserUserId(Long userId);  //Get all the split that is owed by the User
    List<Split> findByExpensePaidUserUserId(Long userId); //Get all the split who owe money to the user

    @Query("SELECT new com.example.FairShare.dto.commonDTO.BalanceDTO(" +
            "s.user.userId, COALESCE(SUM(s.amount), 0)) " +
            "FROM Split s " +
            "WHERE s.expense.group.groupId = :groupId " +
            "GROUP BY s.user.userId")
    List<BalanceDTO> getGroupDebit(@Param("groupId") Long groupId);
}
