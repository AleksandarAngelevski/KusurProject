package com.kusur.Kusur.repository;

import com.kusur.Kusur.model.ExpenseSplit;
import com.kusur.Kusur.model.Group;
import com.kusur.Kusur.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit,Integer> {
    List<ExpenseSplit> getExpenseSplitByUserAndOwedToAndSettled(User user1, User user2,Boolean settled);
    List<ExpenseSplit> getExpenseSplitByUserAndOwedToAndSettledAndGroup(User user1, User user2, Boolean settled, Group group);
}
