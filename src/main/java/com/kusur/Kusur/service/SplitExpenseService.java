package com.kusur.Kusur.service;

import com.kusur.Kusur.dto.ExpenseCreationDto;
import com.kusur.Kusur.dto.ExpenseDto;
import com.kusur.Kusur.dto.UserDetailsDto;
import com.kusur.Kusur.model.Expense;
import com.kusur.Kusur.model.ExpenseSplit;
import com.kusur.Kusur.model.User;
import com.kusur.Kusur.repository.ExpenseRepository;
import com.kusur.Kusur.repository.ExpenseSplitRepository;
import com.kusur.Kusur.repository.UserRepository;
import com.kusur.Kusur.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class SplitExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private ExpenseSplitRepository expenseSplitRepository;
    @Autowired
    private UserRepository userRepository;
    public void SplitExpense(){}
    public Expense createExpense(ExpenseCreationDto expense, @AuthenticationPrincipal CustomUserDetails user){
        Expense expense1 = new Expense(expense.description(),expense.amount(),user.getUser());
        expense1.buildSingleExpense(userRepository.findByUsername(expense.userId()).orElseThrow());
        expenseRepository.save(expense1);
        createExpenseSplit(expense1,user);
        return expense1;
    }
    public void createGroupExpense(Expense expense,@AuthenticationPrincipal UserDetailsDto user){

    }
    private void createExpenseSplit(Expense expense, @AuthenticationPrincipal CustomUserDetails user){
        Double sum = expense.getAmount();
        Double split1 = Math.ceil(sum/2);
        Double split2 = sum - split1;
        ExpenseSplit  expenseSplit1 = new ExpenseSplit(expense,user.getUser(),null,split1);
        ExpenseSplit expenseSplit2 = new ExpenseSplit(expense,expense.getUserReceiver(),null,split2);
        expenseSplitRepository.save(expenseSplit1);
        expenseSplitRepository.save(expenseSplit2);
    }
    public List<Expense> getExpenses(User user){
        List<Expense> expenses = expenseRepository.findByCreator(user);
        expenses.addAll(expenseRepository.findByUserReceiver(user));
        expenses.sort(Comparator.comparing(Expense::creationDate).reversed());
        return expenses;
    }
}
