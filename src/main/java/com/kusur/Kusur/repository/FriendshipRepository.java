package com.kusur.Kusur.repository;

import com.kusur.Kusur.model.Friendship;
import com.kusur.Kusur.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.relational.core.sql.In;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends CrudRepository<Friendship,Long> {
    List<Friendship> findFriendshipsBySender(User sender);
    List<Friendship> findFriendshipsByReceiver(User receiver);
    Optional<Friendship> findBySenderAndReceiver(User sender, User receiver);
    boolean existsBySenderAndReceiver(User sender, User receiver);
}
