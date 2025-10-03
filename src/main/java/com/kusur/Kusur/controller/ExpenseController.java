package com.kusur.Kusur.controller;

import com.kusur.Kusur.dto.ExpenseCreationDto;
import com.kusur.Kusur.dto.ExpenseDto;
import com.kusur.Kusur.model.Expense;
import com.kusur.Kusur.model.GroupMembership;
import com.kusur.Kusur.repository.ExpenseRepository;
import com.kusur.Kusur.repository.GroupMembershipRepository;
import com.kusur.Kusur.repository.GroupRepository;
import com.kusur.Kusur.security.CustomUserDetails;
import com.kusur.Kusur.service.AddFriendService;
import com.kusur.Kusur.service.DataValidationService;
import com.kusur.Kusur.service.SplitExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
public class ExpenseController {
    final String  regex_pattern="^[1-9]\\d*(\\.\\d+)?$";
    @Autowired
    ExpenseRepository expenseRepository;
    @Autowired
    SplitExpenseService splitExpenseService;
    @Autowired
    GroupMembershipRepository groupMembershipRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    DataValidationService dataValidationService;
    @Autowired
    AddFriendService addFriendService;
    @PostMapping("/expense/add")
    public ResponseEntity<ExpenseDto> addExpense(@RequestBody ExpenseCreationDto expense, @AuthenticationPrincipal CustomUserDetails user){
        Expense temp= new Expense();
        System.out.println(expense);

        if(expense.amount()==null || (expense.userId()==null && expense.groupId()==null)||(expense.splitChoice()==null && expense.groupId()==null)|| !expense.amount().toString().matches(regex_pattern) || expense.userId()!=null && expense.splitChoice()!=null && !(expense.splitChoice()>=1 && expense.splitChoice()<=4)){
            return ResponseEntity.badRequest().body(new ExpenseDto(null,null,"Client side error, refresh page",null));
        }
        System.out.println(expense.amount().toString().matches(regex_pattern));
        try{
            System.out.println("USER ID "+ expense.userId());
            System.out.println(expense.userId()==null);
        if(expense.userId() !=null && !expense.userId().equals(user.getUsername())){
            if(!addFriendService.friendshipExists(user.getUsername(),expense.userId())) {
                return ResponseEntity.badRequest().body(new ExpenseDto(null, null, "Client side error, friendship does not exist, refresh page", null));
            }}
        }
        catch (UsernameNotFoundException e){
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(new ExpenseDto(null,null,"Client side error, user not found, refresh page",null));
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
            temp = splitExpenseService.createBinaryExpense(expense,user);
        }else{
            temp = splitExpenseService.createGroupExpense(expense,user);
        }

        return ResponseEntity.ok(new ExpenseDto(temp.getCreator().getUsername(),(temp.getGroup().isPresent()? temp.getGroup().get().getId().toString():null),temp.getDescription(),temp.getAmount()));
    }

    @GetMapping("/expense/{id}")
    public String expense(Model model, @PathVariable Integer id){
        Expense expense = expenseRepository.findById(id);
        model.addAttribute("expense",expense);
        return "expense.html";
    }

}
