package com.kusur.Kusur.dto;

import jdk.jfr.DataAmount;

@DataAmount

public class UserRegistrationDto {
    private String email;
    private String password;
    private String username;
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
