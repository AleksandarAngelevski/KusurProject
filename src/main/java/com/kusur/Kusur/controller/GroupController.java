package com.kusur.Kusur.controller;

import com.kusur.Kusur.dto.GroupCreationDto;
import com.kusur.Kusur.model.Group;
import com.kusur.Kusur.model.GroupMembership;
import com.kusur.Kusur.model.User;
import com.kusur.Kusur.repository.GroupMembershipRepository;
import com.kusur.Kusur.repository.GroupRepository;
import com.kusur.Kusur.security.CustomUserDetails;
import com.kusur.Kusur.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GroupController {
    @Autowired
    GroupService groupService;
    GroupMembershipRepository groupMembershipRepository;
    GroupRepository groupRepository;
    GroupMembership groupMembership;
    @GetMapping("/group/{id}")
    public Group getGroup(@PathVariable Integer id){

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
    @PostMapping("/group/create")
        public ResponseEntity createGroup(@RequestBody GroupCreationDto groupCreationDto, @AuthenticationPrincipal CustomUserDetails customUserDetails){
            groupService.createGroup(groupCreationDto,customUserDetails);
            System.out.println(groupCreationDto.groupName());
            System.out.println(groupCreationDto.users());
            return  ResponseEntity.ok().body("Group created successfully");
        }
}
