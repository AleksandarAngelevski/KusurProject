package com.kusur.Kusur.repository;

import com.kusur.Kusur.model.Expense;
import com.kusur.Kusur.model.Group;
import com.kusur.Kusur.model.User;
import org.hibernate.sql.ast.tree.expression.JdbcParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense,Long> {
    public Expense findById(long id);
    public List<Expense> findByGroup(Group group);
    public List<Expense> findByCreatorAndGroupIsNullAndSettled(User user,Boolean settled);
    public List<Expense> findByUserReceiverAndGroupIsNull(User user);
    public List<Expense> findByPayeeAndGroupIsNullAndSettled(User user,Boolean settled);
    public List<Expense> findExpenseByGroupAndSettled(Group group,Boolean settled);
    public List<Expense> findExpenseByPayeeAndGroupIsNullAndSettled(User user,Boolean settled);
    public List<Expense> findExpenseByUserReceiverAndGroupIsNullAndSettled(User user,Boolean settled);

}
