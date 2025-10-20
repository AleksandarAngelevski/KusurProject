package com.kusur.Kusur.model;

import jakarta.persistence.*;

@Entity
public class GroupNetBalances implements NetBalances{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user1_id")
    private User user1;
    @ManyToOne
    @JoinColumn(name="user2_id")
    private User user2;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
    private Double netDiff;
    public GroupNetBalances() {}
    public GroupNetBalances(User user1, User user2,Group group) {
        this.user1 = user1;
        this.user2 = user2;
        this.group = group;
        this.netDiff = 0.0;
    }
    public void increaseDiff(Double sum){
        this.netDiff+=sum;
    }
    public void decreaseDiff(Double sum){
        this.netDiff-=sum;
    }
    public User getUser1() {
        return this.user1;
    }
    public User getUser2() {
        return this.user2;
    }
    public Group getGroup() {return this.group;}
    public Double getNet_diff() {
        return netDiff;
    }
}
