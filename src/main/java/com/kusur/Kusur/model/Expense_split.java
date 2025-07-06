package com.kusur.Kusur.model;

import jakarta.persistence.*;

@Entity
@Table(name = "expense_splits")
public class Expense_split {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "expense_id")
    private Expense expense;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private Double amount;
    public Expense_split(Expense expense, User user, Double amount) {
        this.expense = expense;
        this.user = user;
        this.amount = amount;
    }
    public Expense_split() {}
}
