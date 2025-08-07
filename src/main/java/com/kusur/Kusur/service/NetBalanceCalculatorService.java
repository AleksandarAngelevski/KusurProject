package com.kusur.Kusur.service;

import com.kusur.Kusur.model.ExpenseSplit;
import com.kusur.Kusur.model.User;
import com.kusur.Kusur.model.UserNetBalances;
import com.kusur.Kusur.repository.ExpenseSplitRepository;
import com.kusur.Kusur.repository.UserNetBalancesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NetBalanceCalculatorService {
    @Autowired
    UserNetBalancesRepository userNetBalancesRepository;
    @Autowired
    ExpenseSplitRepository expenseSplitRepository;

    public  void calculate_net_balance(User user1,User user2){
        User user1temp = (user1.getId()<user2.getId())?user1:user2;
        User user2temp = (user1.getId()>user2.getId())?user1:user2;
        List<ExpenseSplit> expenseSplitsUser1=expenseSplitRepository.getExpenseSplitByUserAndOwedToAndSettled(user1temp,user2temp,false);
        List<ExpenseSplit> expenseSplitsUser2=expenseSplitRepository.getExpenseSplitByUserAndOwedToAndSettled(user2temp,user1temp,false);
        Double net_balance1=0.0;
        Double net_balance2=0.0;
        System.out.println(userNetBalancesRepository.getUserNetBalancesByUser1AndUser2(user1temp,user2temp));
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!______________________________________");
        UserNetBalances userNetBalance = userNetBalancesRepository.getUserNetBalancesByUser1AndUser2(user1temp,user2temp).orElseGet(()-> createNetBalance(user1temp,user2temp));
        System.out.println(expenseSplitsUser1);
        System.out.println(expenseSplitsUser2);
        for(int i=0;i<expenseSplitsUser1.size();i++){
            net_balance1+=expenseSplitsUser1.get(i).getAmount();
            expenseSplitsUser1.get(i).settle();
        }
        for(int i=0;i<expenseSplitsUser2.size();i++){
            net_balance2+=expenseSplitsUser2.get(i).getAmount();
            expenseSplitsUser2.get(i).settle();
        }
        System.out.println("_____________________---");
        System.out.println(net_balance1);
        System.out.println(net_balance2);
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
    private UserNetBalances createNetBalance(User user1,User user2){
        UserNetBalances userNetBalances = new UserNetBalances((user1.getId()<user2.getId()?user1:user2),(user1.getId()>user2.getId()?user1:user2));
        System.out.println("Created net balance row");
        userNetBalancesRepository.save(userNetBalances);
        return  userNetBalances;
    }
    private List<UserNetBalances> getAllBalances(User user){
        List<UserNetBalances> userNetBalances= userNetBalancesRepository.findByUser1(user);
        userNetBalances.addAll(userNetBalancesRepository.findByUser2(user));
        return userNetBalances;
    }
    public List<String> getAllBalancesAsString(User user){
        List<UserNetBalances> userNetBalances = getAllBalances(user);
        List<String> userNetBalancesAsString = userNetBalances.stream().map((e)->{
    
            if(!e.getUser1().equals(user)){
                System.out.println("HERE");
                if(e.getNet_diff()>0){
                    return "You owe "+e.getUser1()+" "+e.getNet_diff()+"MKD";
                }else if(e.getNet_diff()<0){
                    return e.getUser1()+" owes you "+(e.getNet_diff()*-1)+"MKD";
                }
            }else{
                System.out.println("HERE2");
                if(e.getNet_diff()>0){
                    return e.getUser2()+" owes you "+e.getNet_diff()+"MKD";
                }else if(e.getNet_diff()<0){
                    return "You owe "+e.getUser2()+" "+(e.getNet_diff()*-1)+"MKD";
                }
            }
            return null;
        }).filter(e -> e!=null).collect(Collectors.toList());
        return userNetBalancesAsString;
    }
}
