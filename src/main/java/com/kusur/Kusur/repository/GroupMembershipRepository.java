package com.kusur.Kusur.repository;

import com.kusur.Kusur.model.Group;
import com.kusur.Kusur.model.GroupMembership;
import com.kusur.Kusur.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership,Integer> {
    List<GroupMembership> findGroupMembershipByMember(User member);
    List<GroupMembership> findGroupMembershipsByGroup(Group group);
    Optional<GroupMembership> findGroupMembershipByMemberAndGroup(User member,Group group);
}
