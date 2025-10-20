package com.kusur.Kusur.controller;

import com.kusur.Kusur.dto.BalanceDto;
import com.kusur.Kusur.dto.ExpenseCreationDto;
import com.kusur.Kusur.dto.ExpenseDto;
import com.kusur.Kusur.dto.SettlementDto;
import com.kusur.Kusur.model.*;
import com.kusur.Kusur.repository.*;
import com.kusur.Kusur.security.CustomUserDetails;
import com.kusur.Kusur.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class ExpenseController {
    final String  regex_pattern="^[1-9]\\d*(\\.\\d+)?$";
    @Autowired
    ExpenseRepository expenseRepository;
    @Autowired
    SplitExpenseService splitExpenseService;
    @Autowired
    NetBalanceCalculatorService netBalanceCalculatorService;
    @Autowired
    GroupMembershipRepository groupMembershipRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    DataValidationService dataValidationService;
    @Autowired
    AddFriendService addFriendService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    SettlementRepository settlementRepository;
    @Autowired
    SettlementService settlementService;
    @Autowired
    private UserNetBalancesRepository userNetBalancesRepository;
    @Autowired
    private GroupNetBalancesRepository groupNetBalancesRepository;

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
            List<UserNetBalances> unb = userNetBalancesRepository.findByUser1(user.getUser());
            unb.addAll(userNetBalancesRepository.findByUser2(user.getUser()));
            System.out.println("UNB");
            System.out.println(unb);
            if(unb.size()==0){
                List<Expense> expenses =expenseRepository.findExpenseByPayeeAndGroupIsNullAndSettled(user.getUser(),false);
                expenses.addAll(expenseRepository.findExpenseByUserReceiverAndGroupIsNullAndSettled(user.getUser(),false));
                System.out.println(expenses);
                expenses.forEach(exp->{exp.settleExpense();});
                expenseRepository.saveAll(expenses);
            }

        }else{
            System.out.println(")))))))))))))))))))))))))))))))))))))))");
            temp = splitExpenseService.createGroupExpense(expense,user);
            Group gr =groupRepository.findGroupById(Integer.valueOf(expense.groupId())).orElse(null);
            List<GroupNetBalances> gnb=groupNetBalancesRepository.getGroupNetBalancesByGroupAndNetDiffIsNot(gr,0.0);
            if(gnb.size()==0){
                List<Expense> expenses =expenseRepository.findExpenseByGroupAndSettled(gr,false);
                expenses.forEach(exp->{exp.settleExpense();});
                expenseRepository.saveAll(expenses);
            }
        }

        return ResponseEntity.ok(new ExpenseDto(temp.getCreator().getUsername(),(temp.getGroup().isPresent()? temp.getGroup().get().getId().toString():null),temp.getDescription(),temp.getAmount()));
    }

    @GetMapping("/expense/{id}")
    public String expense(Model model, @PathVariable Integer id){
        Expense expense = expenseRepository.findById(id);
        model.addAttribute("expense",expense);
        return "expense.html";
    }
    @PostMapping("/settlePayment")
    public ResponseEntity<String> settleGroupExpense(@RequestBody SettlementDto dto, @AuthenticationPrincipal CustomUserDetails user){
        try {
            System.out.println(dto);
            if (dto.groupId() == null) {
                Settlement settlement = new Settlement(userRepository.findByUsername(dto.debtorName()).orElseThrow(()-> new Exception("User "+dto.debtorName()+" not found")), userRepository.findByUsername(dto.debteeName()).orElseThrow(()-> new Exception("User "+dto.debteeName()+" not found")), dto.amount(), null);
                settlementRepository.save(settlement);
                netBalanceCalculatorService.add_settlement_to_net_balances(settlement);
                List<UserNetBalances> unb = userNetBalancesRepository.findUserNetBalancesByUser1AndNetDiffIsNot(user.getUser(),0.0);
                unb.addAll(userNetBalancesRepository.findUserNetBalancesByUser2AndNetDiffIsNot(user.getUser(),0.0));
                System.out.println("UNB");
                System.out.println(unb);
                if(unb.size()==0){
                    List<Expense> expenses =expenseRepository.findExpenseByPayeeAndGroupIsNullAndSettled(user.getUser(),false);
                    expenses.addAll(expenseRepository.findExpenseByUserReceiverAndGroupIsNullAndSettled(user.getUser(),false));
                    expenses.forEach((e)-> System.out.println(e));
                    expenses.forEach(expense->{expense.settleExpense();});
                    expenseRepository.saveAll(expenses);
                }

                System.out.println("Users settlement");

            } else {
                Settlement settlement = new Settlement(userRepository.findByUsername(dto.debtorName()).orElseThrow(()-> new Exception("User "+dto.debtorName()+" not found")), userRepository.findByUsername(dto.debteeName()).orElseThrow(()-> new Exception("User "+dto.debteeName()+" not found")), dto.amount(), groupRepository.findGroupById(dto.groupId()).orElseThrow(()->new Exception("Group not found")));
                settlementRepository.save(settlement);
                netBalanceCalculatorService.add_settlement_to_net_balances(settlement);
                Group gr =groupRepository.findGroupById(dto.groupId()).orElseThrow(()->new Exception("Group not found"));
                List<GroupNetBalances> gnb=groupNetBalancesRepository.getGroupNetBalancesByGroupAndNetDiffIsNot(gr,0.0);
                if(gnb.size()==0){
                    List<Expense> expenses =expenseRepository.findExpenseByGroupAndSettled(gr,false);
                    expenses.forEach(expense->{expense.settleExpense();});
                    expenseRepository.saveAll(expenses);
                }
                System.out.println("Group settlement");
            }

            return ResponseEntity.badRequest().body("Sucessfull response");
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/netBalances")
    public List<BalanceDto> netBalances(@AuthenticationPrincipal CustomUserDetails user){
        return netBalanceCalculatorService.getAllUserBalances(user.getUser());
    }
}
