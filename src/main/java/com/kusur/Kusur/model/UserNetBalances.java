package com.kusur.Kusur.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class UserNetBalances {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user1")
    private User user1;
    @ManyToOne
    @JoinColumn(name ="user2")
    private User user2;
    private Double net_diff;
    public UserNetBalances() {

    }
    public UserNetBalances(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.net_diff = 0.00;
    }
    public void increaseDiff(Double sum){
        this.net_diff+=sum;
    }
    public void decreaseDiff(Double sum){
        this.net_diff-=sum;
    }
}
