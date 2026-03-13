package com.example.FairShare.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "splits")
public class Split {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long splitId;

    @Column(name = "amount" ,nullable = false)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)    //Unidirectional Relation
    @JoinColumn(name = "user_id",nullable = false)   //user_id is FK
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id",nullable = false)  //expense_id is FK
    private Expense expense;
}


