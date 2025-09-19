package com.kusur.Kusur.model;

import jakarta.persistence.*;

@Table(name= "groupExpense")
@Entity
public class GroupExpense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name="expense")
    private Expense expense;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name="group_id")
    private Group group;
    public GroupExpense() {}
    public GroupExpense(Expense expense,User user,Group group){
        this.expense = expense;
        this.user = user;
        this.group = group;
    }
    public User getUser() {return this.user;}
    public Expense getExpense() {return this.expense;}
    public Group getGroup() {return this.group;}

}
