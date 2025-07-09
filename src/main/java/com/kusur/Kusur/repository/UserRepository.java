package com.kusur.Kusur.repository;
import com.kusur.Kusur.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByVerificationToken(String token);
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
}
