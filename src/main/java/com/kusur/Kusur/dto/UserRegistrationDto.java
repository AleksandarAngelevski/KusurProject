package com.kusur.Kusur.dto;

import jdk.jfr.DataAmount;

@DataAmount

public record UserRegistrationDto(String email, String password, String username) {
    public UserRegistrationDto(){
        this("","","");
    }
}
