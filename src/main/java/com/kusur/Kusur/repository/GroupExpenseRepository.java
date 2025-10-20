package com.kusur.Kusur.repository;

import com.kusur.Kusur.model.GroupExpense;
import com.kusur.Kusur.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupExpenseRepository extends JpaRepository<GroupExpense,Integer> {
    List<GroupExpense> getGroupExpenseByUser(User user);
}
