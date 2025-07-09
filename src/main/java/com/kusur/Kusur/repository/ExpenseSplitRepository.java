package com.kusur.Kusur.repository;

import com.kusur.Kusur.model.ExpenseSplit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit,Integer> {

}
