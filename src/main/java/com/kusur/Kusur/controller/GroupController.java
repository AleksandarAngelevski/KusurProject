package com.kusur.Kusur.controller;

import com.kusur.Kusur.model.Group;
import com.kusur.Kusur.model.GroupMembership;
import com.kusur.Kusur.model.User;
import com.kusur.Kusur.repository.GroupMembershipRepository;
import com.kusur.Kusur.repository.GroupRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GroupController {
    GroupMembershipRepository groupMembershipRepository;
    GroupRepository groupRepository;
    GroupMembership groupMembership;
    @GetMapping("/group/{id}")
    public Group getGroup(){
        return new Group();
    }
    @GetMapping("/group/")
    public List<User> getGroupMembers() {
        return new ArrayList<>();
    }

    @GetMapping("/group/getAll/{username}")
    public List<Group> getAllGroupMemberships(@PathVariable String username){
        return new ArrayList<>();
    }

}
