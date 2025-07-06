package com.kusur.Kusur.controller;

import com.kusur.Kusur.dto.ExpenseDto;
import com.kusur.Kusur.model.Expense;
import com.kusur.Kusur.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExpenseController {

    @PostMapping
    public Expense addExpense(@RequestBody ExpenseDto expense, @AuthenticationPrincipal CustomUserDetails user){
        return new Expense();
    }

}
