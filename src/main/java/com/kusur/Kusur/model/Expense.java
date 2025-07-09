package com.kusur.Kusur.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

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
    @JoinColumn(name ="creator")
    private User creator;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userReceiver;
    private LocalDateTime date;
    public Expense(String description, Double amount,User creator) {
        this.description = description;
        this.amount = amount;
        this.creator = creator;
        this.group = null;
        this.userReceiver = null;
        this.date = LocalDateTime.now();

    }
    public Expense() {}
    public void buildSingleExpense(User receiver) {
        this.userReceiver = receiver;
    }
    public void buildGroupExpense(Group group) {
        this.group = group;
    }
    public Double getAmount() {
        return this.amount;
    }
    public User getUserReceiver() {return this.userReceiver;}
    public LocalDateTime creationDate() {
        return this.date;
    }

    public Optional<Group> getGroup() {
        return Optional.ofNullable(this.group);
    }

    public String getDescription() {
        return description;
    }

    public User getCreator() {
        return creator;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
