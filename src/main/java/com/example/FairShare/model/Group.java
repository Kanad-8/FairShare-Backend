package com.example.FairShare.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "name")
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name ="group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL) //group_id is the FK in Expense
    private List<Expense> expenses = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void addUser(User user){
        this.members.add(user);  //Add the user to group members
        user.getGroup().add(this); //Add the group to user group list
    }

    public void addExpense(Expense expense){
        this.expenses.add(expense);  //Add expense to expenses list
        expense.setGroup(this);      //set the group of expense
    }
}


