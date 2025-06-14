package com.kusur.Kusur.repository;

import com.kusur.Kusur.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends CrudRepository<Friendship,Long> {
    List<Friendship> findFriendshipsByUser_Id(String id);
    Optional<Friendship> findBySenderAndReceiver(String sender, String receiver);
}
