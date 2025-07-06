package com.kusur.Kusur.model;

import jakarta.persistence.*;

@Entity
@Table(name="Expenses")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn( name = "group_id")
    private Group group;
    private String description;
    private Double amount;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User creator;
    private Boolean settled;
    public Expense(Group group, String description, Double amount, User creator, Boolean settled) {
        this.group = group;
        this.description = description;
        this.amount = amount;
        this.creator = creator;
        this.settled = settled;
    }
    public Expense(Boolean settled, User creator, Double amount, String description) {
        this.settled = settled;
        this.creator = creator;
        this.amount = amount;
        this.description = description;
    }
    public Expense() {}
}
