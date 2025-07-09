package com.kusur.Kusur.controller;

import com.kusur.Kusur.dto.ExpenseCreationDto;
import com.kusur.Kusur.dto.ExpenseDto;
import com.kusur.Kusur.model.Expense;
import com.kusur.Kusur.security.CustomUserDetails;
import com.kusur.Kusur.service.SplitExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExpenseController {
    @Autowired
    SplitExpenseService splitExpenseService;
    @PostMapping("/expense/add")
    public ResponseEntity<ExpenseDto> addExpense(@RequestBody ExpenseCreationDto expense, @AuthenticationPrincipal CustomUserDetails user){
        Expense temp= new Expense();
        if(expense.groupId()==null){
            temp = splitExpenseService.createExpense(expense,user);
        }else{

        }

        return ResponseEntity.ok(new ExpenseDto(temp.getCreator().getUsername(),(temp.getGroup().isPresent()? temp.getGroup().get().getId().toString():null),temp.getDescription(),temp.getAmount()));
    }

}
