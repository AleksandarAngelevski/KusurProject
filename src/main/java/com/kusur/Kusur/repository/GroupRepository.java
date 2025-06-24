package com.kusur.Kusur.repository;

import com.kusur.Kusur.model.Group;
import com.kusur.Kusur.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GroupRepository extends CrudRepository<Group,Integer> {
    Optional<Group> findGroupByOwner(User owner);
    Optional<Group> findGroupByName(String name);
    Optional<Group> findGroupById(Integer id);
}
