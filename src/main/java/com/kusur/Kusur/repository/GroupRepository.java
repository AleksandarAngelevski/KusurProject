package com.kusur.Kusur.repository;

import com.kusur.Kusur.model.Group;
import com.kusur.Kusur.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group,Integer> {
    Optional<Group> findGroupByOwner(User owner);
    Optional<Group> findGroupByName(String name);
    Optional<Group> findGroupById(Integer id);
}
