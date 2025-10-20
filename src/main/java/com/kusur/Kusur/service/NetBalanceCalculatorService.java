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
import org.springframework.jdbc.core.metadata.Db2CallMetaDataProvider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
    public void add_settlement_to_net_balances(Settlement settlement){
        User user1temp = (settlement.getPayer().getId()<settlement.getPayee().getId())?settlement.getPayer():settlement.getPayee();
        User user2temp = (settlement.getPayer().getId()>settlement.getPayee().getId())?settlement.getPayer():settlement.getPayee();
        NetBalances netBalances;
        if(settlement.getGroup()==null){
            netBalances = userNetBalancesRepository.getUserNetBalancesByUser1AndUser2(user1temp,user2temp).orElseGet(()-> createNetBalance(user1temp,user2temp));
        }else{
            netBalances = groupNetBalancesRepository.getGroupNetBalancesByUser1AndUser2AndGroup(user1temp,user2temp,settlement.getGroup()).orElseGet(()-> createGroupNetBalance(user1temp,user2temp,settlement.getGroup()));

        }
        if(settlement.getPayee().equals(user1temp)){
            netBalances.decreaseDiff(Double.valueOf(settlement.getAmount()));
        }else{
            netBalances.increaseDiff(Double.valueOf(settlement.getAmount()));
        }
        if(settlement.getGroup()!=null){
            groupNetBalancesRepository.save((GroupNetBalances) netBalances);
        }else{
            userNetBalancesRepository.save((UserNetBalances) netBalances);
        }

    }


    public Double getUserGroupBalance(Group group,User user){
        Double amount=0.00;
        List<GroupNetBalances> netBalances = groupNetBalancesRepository.getGroupNetBalancesByUser1AndGroup(user,group);
        for(int i =0;i<netBalances.size();i++){
            amount+=netBalances.get(i).getNet_diff();
        }
        netBalances=groupNetBalancesRepository.getGroupNetBalancesByUser2AndGroup(user,group);
        for(int i =0;i<netBalances.size();i++){
            amount-=netBalances.get(i).getNet_diff();
        }
        return  amount;


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
    public List<BalanceDto> getAllUserBalances(User user){
        List<UserNetBalances> userNetBalances = getAllBalances(user);
        List<BalanceDto> userNetBal = userNetBalances.stream().map((e)->{
            if(!e.getUser1().equals(user)){
                System.out.println("HERE");
                if(e.getNet_diff()>0){
                    return netBalanceToDto.balanceDto(userToUserDetailsDto.outUser(e.getUser2()),userToUserDetailsDto.outUser(e.getUser1()),"You owe "+e.getUser1()+" "+e.getNet_diff()+"MKD",Math.abs(e.getNet_diff()));
                }else if(e.getNet_diff()<0){
                    return netBalanceToDto.balanceDto(userToUserDetailsDto.outUser(e.getUser1()),userToUserDetailsDto.outUser(e.getUser2()), e.getUser1()+" owes you "+(e.getNet_diff()*-1)+"MKD",Math.abs(e.getNet_diff()));
                }
            }else{
                System.out.println("HERE2");
                if(e.getNet_diff()>0){
                    return netBalanceToDto.balanceDto(userToUserDetailsDto.outUser(e.getUser2()),userToUserDetailsDto.outUser(e.getUser1()), e.getUser2()+" owes you "+e.getNet_diff()+"MKD",Math.abs(e.getNet_diff()));
                }else if(e.getNet_diff()<0){
                    return netBalanceToDto.balanceDto(userToUserDetailsDto.outUser(e.getUser1()),userToUserDetailsDto.outUser(e.getUser2()),  "You owe "+e.getUser2()+" "+(e.getNet_diff()*-1)+"MKD",Math.abs(e.getNet_diff()));
                }
            }
            return null;
        }).filter(e -> e!=null).collect(Collectors.toList());
        return userNetBal;
    }
    public List<String> getAllGroupBalances(User user){
        return new ArrayList<String>();
    }
    public List<BalanceDto>     getGroupBalances(User user, Group group,Boolean onlyUserInvolved){
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
