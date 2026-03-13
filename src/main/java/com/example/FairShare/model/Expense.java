package com.example.FairShare.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private Long expenseId;

    private String description;

    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)  //Unidirectional Relation
    @JoinColumn(name = "paid_user")
    private User paidUser;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL , orphanRemoval = true)  //expense if FK of Split
    private List<Split> splits = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")       //FK of Group
    private Group group;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void addSplit(Split split){
        split.setExpense(this);
        this.splits.add(split);
    }

    public void removeSplit(Split split){
        this.splits.remove(split);
    }

}
