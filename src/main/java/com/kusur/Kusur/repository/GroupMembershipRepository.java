package com.kusur.Kusur.repository;

import com.kusur.Kusur.model.Group;
import com.kusur.Kusur.model.GroupMembership;
import com.kusur.Kusur.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GroupMembershipRepository extends CrudRepository<GroupMembership,Integer> {
    Optional<GroupMembership> findGroupMembershipByMember(User member);
    Optional<GroupMembership> findGroupMembershipByGroup(Group group);
}
