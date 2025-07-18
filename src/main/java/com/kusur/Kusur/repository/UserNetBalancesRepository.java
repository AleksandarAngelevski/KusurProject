package com.kusur.Kusur.repository;

import com.kusur.Kusur.model.User;
import com.kusur.Kusur.model.UserNetBalances;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserNetBalancesRepository extends JpaRepository<UserNetBalances,Long> {
    Optional<UserNetBalances> findByUser1AndUser2(User user1, User user2);

}
