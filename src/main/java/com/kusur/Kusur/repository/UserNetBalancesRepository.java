package com.kusur.Kusur.repository;

import com.kusur.Kusur.model.User;
import com.kusur.Kusur.model.UserNetBalances;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserNetBalancesRepository extends JpaRepository<UserNetBalances,Long> {
    Optional<UserNetBalances> getUserNetBalancesByUser1AndUser2(User user1, User user2);
    List<UserNetBalances> findByUser1(User user);
    List<UserNetBalances> findByUser2(User user);

}
