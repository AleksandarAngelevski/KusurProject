package com.kusur.Kusur.controller;

import com.kusur.Kusur.dto.ExpenseCreationDto;
import com.kusur.Kusur.dto.ExpenseDto;
import com.kusur.Kusur.model.Expense;
import com.kusur.Kusur.model.GroupMembership;
import com.kusur.Kusur.repository.GroupMembershipRepository;
import com.kusur.Kusur.repository.GroupRepository;
import com.kusur.Kusur.security.CustomUserDetails;
import com.kusur.Kusur.service.SplitExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
public class ExpenseController {
    final String  regex_pattern="^[1-9]\\d*(\\.\\d+)?$";

    @Autowired
    SplitExpenseService splitExpenseService;
    @Autowired
    GroupMembershipRepository groupMembershipRepository;
    @Autowired
    GroupRepository groupRepository;
    @PostMapping("/expense/add")
    public ResponseEntity<ExpenseDto> addExpense(@RequestBody ExpenseCreationDto expense, @AuthenticationPrincipal CustomUserDetails user){
        Expense temp= new Expense();
        System.out.println(expense);
        System.out.println(expense.amount().toString().matches(regex_pattern));
        if(expense.amount()==null || (expense.userId()==null && expense.groupId()==null)|| !expense.amount().toString().matches(regex_pattern)){
            return ResponseEntity.badRequest().body(new ExpenseDto(null,null,"Client side error, refresh page",null));
        }
        if(expense.groupId()==null){

        }
        else {
            try {
                if (!groupMembershipRepository.findGroupMembershipByMemberAndGroup(user.getUser(), groupRepository.findGroupById(Integer.valueOf(expense.groupId())).orElseThrow()).isPresent()) {
                    return ResponseEntity.badRequest().body(new ExpenseDto(null, null, "Not your group!!!", null));
                }

            } catch (NoSuchElementException e) {
                return ResponseEntity.badRequest().body(new ExpenseDto(null, null, "Group does not exist!", null));
            }
        }

        if(expense.groupId()==null){
            temp = splitExpenseService.createExpense(expense,user);
        }else{

        }

        return ResponseEntity.ok(new ExpenseDto(temp.getCreator().getUsername(),(temp.getGroup().isPresent()? temp.getGroup().get().getId().toString():null),temp.getDescription(),temp.getAmount()));
    }

}
