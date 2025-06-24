package com.kusur.Kusur.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;


    private String username;
    private String password;
    private String email;
    private boolean enabled;
    private String verificationToken;
    private String nickname;
    public User(){}
    public User(String username,String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    @OneToMany(mappedBy = "Id")
    private List<GroupMembership> groupMemberships;
    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }
    public String getNickname() {
        return this.nickname;
    }
    public List<GroupMembership> getGroupMemberships() {
        return this.groupMemberships;
    }
    public void setNickname(String uniqueId) {
        this.nickname = uniqueId;
    }
    public Integer getId() {
        return this.id;
    }
    @Override
    public String toString() {
        return this.username;
    }
}
