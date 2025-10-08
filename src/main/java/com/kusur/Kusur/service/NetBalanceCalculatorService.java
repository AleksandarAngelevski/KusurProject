package com.kusur.Kusur.service;

import com.kusur.Kusur.dto.BalanceDto;
import com.kusur.Kusur.dto.GroupCreationDto;
import com.kusur.Kusur.dto.UserDetailsDto;
import com.kusur.Kusur.mapper.NetBalanceToDto;
import com.kusur.Kusur.mapper.UserToUserDetailsDto;
import com.kusur.Kusur.model.*;
import com.kusur.Kusur.repository.ExpenseSplitRepository;
import com.kusur.Kusur.repository.GroupNetBalancesRepository;
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
    GroupNetBalancesRepository groupNetBalancesRepository;
    @Autowired
    ExpenseSplitRepository expenseSplitRepository;

    @Autowired
    UserToUserDetailsDto userToUserDetailsDto;
    @Autowired
    NetBalanceToDto netBalanceToDto;
    public  void calculate_net_balance(User user1, User user2, Group group){
        User user1temp = (user1.getId()<user2.getId())?user1:user2;
        User user2temp = (user1.getId()>user2.getId())?user1:user2;
        List<ExpenseSplit> expenseSplitsUser1;
        List<ExpenseSplit> expenseSplitsUser2;
        Double net_balance1=0.0;
        Double net_balance2=0.0;
        NetBalances userNetBalance;
        if(group!=null){
            System.out.println("Group net ");
             expenseSplitsUser1=expenseSplitRepository.getExpenseSplitByUserAndOwedToAndSettledAndGroup(user1temp,user2temp,false,group);
             expenseSplitsUser2=expenseSplitRepository.getExpenseSplitByUserAndOwedToAndSettledAndGroup(user2temp,user1temp,false,group);
            userNetBalance= groupNetBalancesRepository.getGroupNetBalancesByUser1AndUser2AndGroup(user1temp,user2temp,group
            ).orElseGet(()-> createGroupNetBalance(user1temp,user2temp,group));
        }else{
            expenseSplitsUser1=expenseSplitRepository.getExpenseSplitByUserAndOwedToAndSettled(user1temp,user2temp,false);
            expenseSplitsUser2=expenseSplitRepository.getExpenseSplitByUserAndOwedToAndSettled(user2temp,user1temp,false);
            userNetBalance= userNetBalancesRepository.getUserNetBalancesByUser1AndUser2(user1temp,user2temp).orElseGet(()-> createNetBalance(user1temp,user2temp));
        }

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
        if(group!=null){
            groupNetBalancesRepository.save((GroupNetBalances) userNetBalance);
        }else{
            userNetBalancesRepository.save((UserNetBalances) userNetBalance);
        }


    }
    public void calculate_net_profits_by_user(){

    }
//    public List<Pair<User, ExpenseSplit>> construct_net_differences_graph_group(){
//
//    }
    public  GroupNetBalances createGroupNetBalance(User user1,User user2,Group group) {
        GroupNetBalances groupNetBalances = new GroupNetBalances((user1.getId() < user2.getId() ? user1 : user2), (user1.getId() > user2.getId() ? user1 : user2),group);
        System.out.println("Created group net balance row");
        groupNetBalancesRepository.save(groupNetBalances);
        return groupNetBalances;
    }
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
    public List<String> getAllUserBalancesAsString(User user){
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
    public List<String> getAllGroupBalancesAsString(User user){
        return new ArrayList<String>();
    }
    public List<BalanceDto>     getGroupBalancesAsString(User user, Group group,Boolean onlyUserInvolved){
        List<GroupNetBalances> groupBalances = groupNetBalancesRepository.getGroupNetBalancesByGroup(group);
        System.out.println(group + " Group");
        System.out.println(groupBalances+ " GroupBalances");
        return  groupBalances.stream().map((balance)->{
            if(balance.getUser1().equals(user)){
                if(balance.getNet_diff()>0){
                    return netBalanceToDto.balanceDto(userToUserDetailsDto.outUser(balance.getUser2()),userToUserDetailsDto.outUser(balance.getUser1()),balance.getUser2()+" owes you "+balance.getNet_diff()+"MKD",Math.abs(balance.getNet_diff()));
                }else if(balance.getNet_diff()<0){
                    return netBalanceToDto.balanceDto(userToUserDetailsDto.outUser(balance.getUser1()),userToUserDetailsDto.outUser(balance.getUser2()),"You owe "+balance.getUser2()+" " +Math.abs(balance.getNet_diff())+"MKD",Math.abs(balance.getNet_diff()));
                }
            }else if(balance.getUser2().equals(user)){
                    if(balance.getNet_diff()>0){
                        return netBalanceToDto.balanceDto(userToUserDetailsDto.outUser(balance.getUser2()),userToUserDetailsDto.outUser(balance.getUser1()),"You owe "+balance.getUser1()+" " +balance.getNet_diff()+"MKD",Math.abs(balance.getNet_diff()));
                    }else if(balance.getNet_diff()<0){
                        return netBalanceToDto.balanceDto(userToUserDetailsDto.outUser(balance.getUser1()),userToUserDetailsDto.outUser(balance.getUser2()),balance.getUser1()+" owes you "+Math.abs(balance.getNet_diff())+"MKD",Math.abs(balance.getNet_diff()));

                    }

            }else if(!onlyUserInvolved){
                if(balance.getNet_diff()>0){
                    return netBalanceToDto.balanceDto(userToUserDetailsDto.outUser(balance.getUser2()),userToUserDetailsDto.outUser(balance.getUser1()),balance.getUser2()+" owes "+balance.getUser1()+" " +balance.getNet_diff()+"MKD",Math.abs(balance.getNet_diff()));
                }else if(balance.getNet_diff()<0){
                    return netBalanceToDto.balanceDto(userToUserDetailsDto.outUser(balance.getUser1()),userToUserDetailsDto.outUser(balance.getUser1()),balance.getUser1()+" owes "+balance.getUser2()+" "+Math.abs(balance.getNet_diff())+"MKD",Math.abs(balance.getNet_diff()));
                }
            }
            return null;
        }).filter((e) -> e !=null).collect(Collectors.toList());
    }
}
