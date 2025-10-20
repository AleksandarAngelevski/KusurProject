package com.kusur.Kusur.dto;

public record BalanceDto(UserDetailsDto debtor,UserDetailsDto debtee,String message,Double amount){}
