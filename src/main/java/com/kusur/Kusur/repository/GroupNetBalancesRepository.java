package com.kusur.Kusur.repository;

import com.kusur.Kusur.model.Group;
import com.kusur.Kusur.model.GroupNetBalances;
import com.kusur.Kusur.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupNetBalancesRepository extends JpaRepository<GroupNetBalances,Long> {
    Optional<GroupNetBalances> getGroupNetBalancesByUser1AndUser2(User user1,User user2);
    List<GroupNetBalances> getGroupNetBalancesByUser1AndGroup(User user, Group group);
    List<GroupNetBalances> getGroupNetBalancesByUser2AndGroup(User user, Group group);
    Optional<GroupNetBalances> getGroupNetBalancesByUser1AndUser2AndGroup(User user1,User user2,Group group);
    List<GroupNetBalances> getGroupNetBalancesByGroup(Group group);
    List<GroupNetBalances> getGroupNetBalancesByGroupAndNetDiffIsNot(Group group, Double net_diff);



}


