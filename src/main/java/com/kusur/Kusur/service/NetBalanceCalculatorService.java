package com.kusur.Kusur.service;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.kusur.Kusur.model.ExpenseSplit;
import com.kusur.Kusur.model.User;
import com.kusur.Kusur.model.UserNetBalances;
import com.kusur.Kusur.repository.ExpenseSplitRepository;
import com.kusur.Kusur.repository.UserNetBalancesRepository;
import org.hibernate.graph.Graph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NetBalanceCalculatorService {
    @Autowired
    UserNetBalancesRepository userNetBalancesRepository;
    @Autowired
    ExpenseSplitRepository expenseSplitRepository;

    public  void calculate_net_balance(User user1,User user2){
        User user1temp = (user1.getId()<user2.getId())?user1:user2;
        User user2temp = (user1.getId()>user2.getId())?user1:user2;
        List<ExpenseSplit> expenseSplitsUser1=expenseSplitRepository.getExpenseSplitByUserAndOwedToAndSettled(user1temp,user2temp,true);
        List<ExpenseSplit> expenseSplitsUser2=expenseSplitRepository.getExpenseSplitByUserAndOwedToAndSettled(user2temp,user1temp,true);
        Double net_balance1=0.0;
        Double net_balance2=0.0;
        UserNetBalances userNetBalance = userNetBalancesRepository.findByUser1AndUser2(user1temp,user2temp).orElse(createNetBalance(user1,user2));

        for(int i=0;i<expenseSplitsUser1.size();i++){
            net_balance1+=expenseSplitsUser1.get(i).getAmount();
            expenseSplitsUser1.get(i).settle();
        }
        for(int i=0;i<expenseSplitsUser2.size();i++){
            net_balance2+=expenseSplitsUser2.get(i).getAmount();
            expenseSplitsUser1.get(i).settle();
        }
        userNetBalance.decreaseDiff(net_balance1);
        userNetBalance.increaseDiff(net_balance2);
        expenseSplitRepository.saveAll(expenseSplitsUser1);
        expenseSplitRepository.saveAll(expenseSplitsUser2);
        userNetBalancesRepository.save(userNetBalance);

    }
    public void calculate_net_profits_by_user(){

    }
//    public List<Pair<User, ExpenseSplit>> construct_net_differences_graph_group(){
//
//    }
    public UserNetBalances createNetBalance(User user1,User user2){
        UserNetBalances userNetBalances = new UserNetBalances((user1.getId()<user2.getId()?user1:user2),(user1.getId()>user2.getId()?user1:user2));
        userNetBalancesRepository.save(userNetBalances);
        return  userNetBalances;
    }
    
    
}
