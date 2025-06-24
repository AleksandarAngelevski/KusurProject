package com.kusur.Kusur.model;

import jakarta.persistence.*;
@Table(name = "memberships")
@Entity
public class GroupMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;
    @ManyToOne
    @JoinColumn(name= "toGroup")
    private Group group;
    @ManyToOne
    @JoinColumn(name= "member")
    private User member;
    public GroupMembership(Group group, User member) {
        this.group = group;
        this.member = member;
    }

    public GroupMembership() {

    }

    public Integer getId() {
        return Id;
    }
    public Group getGroup() {
        return group;
    }
    public User getMember() {
        return member;
    }

}
