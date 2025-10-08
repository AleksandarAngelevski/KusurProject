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
    private String splitChoice;
    @ManyToOne
    @JoinColumn(name ="creator")
    private User creator;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userReceiver;
    @ManyToOne
    @JoinColumn(name = "payee_id")
    private User payee;
    private LocalDateTime date;
    private Boolean settled;

    public User getPayee() {
        return payee;
    }

    public void setPayee(User payee) {
        this.payee = payee;
    }


    public Expense(String description, Double amount,User creator,User payee) {
        this.description = description;
        this.amount = amount;
        this.creator = creator;
        this.group = null;
        this.payee = payee;
        this.userReceiver = null;
        this.date = LocalDateTime.now();
        this.splitChoice=null;
        this.settled=false;
    }
    public Expense() {}
    public void buildSingleExpense(User receiver,Integer choiceIndex) {
        String[] choices ={"You paid, split equally.","You are owed the full amount.",receiver.getUsername()+" paid,split equally.",receiver.getUsername()+" is owed the full amount."};
        this.userReceiver = receiver;
        this.splitChoice = choices[choiceIndex-1];

    }
    public Integer getId() {
        return this.id;
    }
    public void buildGroupExpense(Group group,User receiver) {
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
