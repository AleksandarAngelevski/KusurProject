package com.kusur.Kusur.model;

import jakarta.persistence.*;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Table(name = "groups")
@Entity
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    @ManyToOne
    @JoinColumn(name= "owner")
    private User owner;

    @OneToMany(mappedBy = "group")
    private List<GroupMembership> members;
    @Override
    public String toString() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<GroupMembership> getMembers() {
        return members;
    }

    public void setMembers(List<GroupMembership> members) {
        this.members = members;
    }
}
