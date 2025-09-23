package com.kusur.Kusur.service;

import com.kusur.Kusur.dto.ExpenseCreationDto;
import com.kusur.Kusur.dto.UserDetailsDto;
import com.kusur.Kusur.model.*;
import com.kusur.Kusur.repository.*;
import com.kusur.Kusur.security.CustomUserDetails;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToFile;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private GroupExpenseRepository groupExpenseRepository;
    public void SplitExpense(){}
    public Expense createBinaryExpense(ExpenseCreationDto expense, @AuthenticationPrincipal CustomUserDetails user){
        Expense expense1 = new Expense(expense.description(),expense.amount(),user.getUser(),userRepository.findByUsername(expense.payee()).orElseThrow());
        expense1.buildSingleExpense(userRepository.findByUsername(expense.userId()).orElseThrow(),expense.splitChoice());
        expenseRepository.save(expense1);
        createBinaryExpenseSplit(expense1,user,expense.splitChoice());
        return expense1;
    }
    public Expense createGroupExpense(ExpenseCreationDto expense,@AuthenticationPrincipal CustomUserDetails user){
        Expense expense1 = new Expense(expense.description(),expense.amount(),user.getUser(),userRepository.findByUsername(expense.payee()).orElseThrow());
        expense1.buildGroupExpense(groupRepository.findGroupById(Integer.valueOf(expense.groupId())).orElseThrow(),userRepository.findByUsername(expense.payee()).orElseThrow());
        expenseRepository.save(expense1);
        List<User> users_in_expense= new LinkedList<>();
        expense.users().forEach(username->{
            User userTemp =userRepository.findByUsername(username).orElseThrow();
            users_in_expense.add(userTemp);
            groupExpenseRepository.save(new GroupExpense(expense1,userTemp,groupRepository.findGroupById(Integer.valueOf(expense.groupId())).orElseThrow()));
        });
        createGroupExpenseSplit(expense1,user,users_in_expense);
        return expense1;
    }
    private void createBinaryExpenseSplit(@NotNull Expense expense, @AuthenticationPrincipal CustomUserDetails user, Integer choice){
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
        netBalanceCalculatorService.calculate_net_balance(expense.getUserReceiver(),user.getUser(),null);
    }
    private void createGroupExpenseSplit(@NotNull Expense expense, @AuthenticationPrincipal CustomUserDetails user, @NotNull List<User> users){
        List<ExpenseSplit> expenseSplits = new LinkedList<>();
        Double sum = expense.getAmount();
        System.out.println("TOTAL AMOUNT: "+sum);
        Double part = Math.floor(expense.getAmount()/users.size());
        System.out.println("PART AMOUNT: "+part);
        for(int i=0;i<users.size();i++) {
            if (users.get(i).equals(expense.getPayee())) {
                System.out.println("Expense creator included in expense split");
                sum = sum - part;
            } else {
                System.out.println("SUM PRE:=> " + sum);
                sum = sum - part;
                System.out.println("SUM AFTER:=> " + sum);
                expenseSplits.add(new ExpenseSplit(expense, users.get(i), expense.getGroup().get(), part, expense.getPayee()));
            }
        }
        for(int i=0;i<expense.getAmount()%users.size();i++){
            expenseSplits.get(i).increaseAmount(1.00);
            sum-=1;
        }

        expenseSplitRepository.saveAll(expenseSplits);
        for(int i=0;i<users.size();i++){
            if(users.get(i).equals(expense.getPayee())){

            }else{
                netBalanceCalculatorService.calculate_net_balance(expense.getPayee(),users.get(i),expense.getGroup().get());
            }

        }

    }
    public List<Expense> getGroupExpenses(User user, Group group){
        List<Expense> expenses = expenseRepository.findByGroup(group);
        expenses.sort(Comparator.comparing(Expense::creationDate).reversed());
        return expenses;
    }       
    public List<Expense> getAllExpenses(User user){
        List<Expense> expenses = expenseRepository.findByCreator(user);
        expenses.addAll(expenseRepository.findByUserReceiver(user));
        expenses.addAll(groupExpenseRepository.getGroupExpenseByUser(user).stream().map(ge ->{
            return  ge.getExpense();
        }).toList());
        expenses.sort(Comparator.comparing(Expense::creationDate).reversed());
        return expenses;
    }
}
