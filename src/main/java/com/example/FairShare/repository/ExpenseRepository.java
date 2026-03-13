package com.example.FairShare.repository;

import com.example.FairShare.dto.commonDTO.BalanceDTO;
import com.example.FairShare.model.Expense;
import com.example.FairShare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense,Long> {

    List<Expense> findByPaidUser(User paidUser);    //Get all Expenses paid by the User4
    List<Expense> findByGroupGroupId(Long groupId);       //Get all Expense for a Group

    @Query("SELECT new com.example.FairShare.dto.commonDTO.BalanceDTO(" +
            "e.paidUser.userId, COALESCE(SUM(e.amount), 0)) " +
            "FROM Expense e " +
            "WHERE e.group.groupId = :groupId " +
            "GROUP BY e.paidUser.userId")
    List<BalanceDTO> getGroupCredit(@Param("groupId") Long groupId);

}
