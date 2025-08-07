package com.kusur.Kusur.model;

import jakarta.persistence.*;

@Entity
@Table(name = "expense_splits")
public class ExpenseSplit {
    @Id
    @JoinColumn(name = "expense_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private Expense expense;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name ="owed_to")
    private User owedTo;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
    private Double amount;
    private Boolean settled;
    public ExpenseSplit(Expense expense, User user,Group group, Double amount,User owedTo) {
        this.expense = expense;
        this.user = user;
        this.amount = amount;
        this.group = group;
        this.owedTo = owedTo;
        this.settled = false;
    }
    public ExpenseSplit() {}
    public void settleExpense() {
        this.settled = true;
    }
    public Double getAmount() {
        return this.amount;
    }
    public void settle(){
        this.settled = true;
    }
}
