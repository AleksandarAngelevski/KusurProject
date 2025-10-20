package com.kusur.Kusur.model;

import jakarta.persistence.*;

@Entity
@Table(name = "settlements")
public class Settlement {
    @Id
    @JoinColumn(name = "settlement_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @ManyToOne
    @JoinColumn(name = "payer")
    User payer;
    @ManyToOne
    @JoinColumn(name = "payee")
    User payee;
    @JoinColumn(name = "amount")
    Float amount;
    @ManyToOne
    @JoinColumn(name = "group_id")
    Group group;
    public Settlement() {

    }
    public Settlement(User payer, User payee, Float amount,Group group) {
        this.payer = payer;
        this.payee = payee;
        this.amount = amount;
        this.group = group;
    }

    public User getPayer() {
        return payer;
    }

    public void setPayer(User payer) {
        this.payer = payer;
    }

    public User getPayee() {
        return payee;
    }

    public void setPayee(User payee) {
        this.payee = payee;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
