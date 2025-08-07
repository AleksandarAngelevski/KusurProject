package com.kusur.Kusur.service;

import com.kusur.Kusur.dto.ExpenseCreationDto;
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
import org.springframework.stereotype.Service;
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
    @Autowired
    private NetBalanceCalculatorService netBalanceCalculatorService;

    public void SplitExpense(){}
    public Expense createExpense(ExpenseCreationDto expense, @AuthenticationPrincipal CustomUserDetails user){
        Expense expense1 = new Expense(expense.description(),expense.amount(),user.getUser());
        expense1.buildSingleExpense(userRepository.findByUsername(expense.userId()).orElseThrow(),expense.splitChoice());
        expenseRepository.save(expense1);
        createBinaryExpenseSplit(expense1,user,expense.splitChoice());
        return expense1;
    }
    public void createGroupExpense(Expense expense,@AuthenticationPrincipal UserDetailsDto user){

    }
    private void createBinaryExpenseSplit(Expense expense, @AuthenticationPrincipal CustomUserDetails user,Integer choice){
        ExpenseSplit expenseSplit1 = new ExpenseSplit();
        Double sum = expense.getAmount();
        Double split1 = Math.ceil(sum/2);
        Double split2 = sum - split1;

        if(choice==1 || choice==3){
            split1 = Math.ceil(sum/2);
            split2 = sum - split1;
            if(choice==1){
                expenseSplit1 = new ExpenseSplit(expense,expense.getUserReceiver(),null,split1,user.getUser());
            }
            else{
                expenseSplit1 = new ExpenseSplit(expense,user.getUser(),null,split1,expense.getUserReceiver());
            }
        }
        else{
            split1 = sum;
                if(choice==2){
                    expenseSplit1 = new ExpenseSplit(expense,expense.getUserReceiver(),null,split1,user.getUser());
                }else{
                    expenseSplit1 = new ExpenseSplit(expense,user.getUser(),null,split1,expense.getUserReceiver());
                }
        }

        expenseSplitRepository.save(expenseSplit1);
        netBalanceCalculatorService.calculate_net_balance(expense.getCreator(),expense.getUserReceiver());
    }
    public List<Expense> getExpenses(User user){
        List<Expense> expenses = expenseRepository.findByCreator(user);
        expenses.addAll(expenseRepository.findByUserReceiver(user));
        expenses.sort(Comparator.comparing(Expense::creationDate).reversed());
        return expenses;
    }
}
