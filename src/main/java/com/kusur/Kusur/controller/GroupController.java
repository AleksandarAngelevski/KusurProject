package com.kusur.Kusur.controller;

import com.kusur.Kusur.dto.BalanceDto;
import com.kusur.Kusur.dto.GroupCreationDto;
import com.kusur.Kusur.dto.GroupDto;
import com.kusur.Kusur.dto.UserDetailsDto;
import com.kusur.Kusur.model.*;
import com.kusur.Kusur.repository.GroupMembershipRepository;
import com.kusur.Kusur.repository.GroupRepository;
import com.kusur.Kusur.repository.UserRepository;
import com.kusur.Kusur.security.CustomUserDetails;
import com.kusur.Kusur.service.GroupService;
import com.kusur.Kusur.service.NetBalanceCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class GroupController {
    @Autowired
    NetBalanceCalculatorService netBalancesService;
    @Autowired
    GroupService groupService;
    @Autowired
    GroupMembershipRepository groupMembershipRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    UserRepository userRepository;
    GroupMembership groupMembership;
    @GetMapping("/group/{id}")
    public Group getGroup(@PathVariable Integer id){
        return new Group();
    }
    @GetMapping("/group/")
    public List<User> getGroupMembers() {
        return new ArrayList<>();
    }

    @GetMapping("/group/getAll")
    public List<GroupDto> getAllGroupMemberships(@AuthenticationPrincipal CustomUserDetails principal){
        List<GroupDto> groups = new ArrayList<>(groupMembershipRepository.findGroupMembershipByMember(principal.getUser()).stream().map(m -> m.getGroup()).map(e -> new GroupDto(e.getName(),e.getId())).collect(Collectors.toList()));
        System.out.println(groups);
        return groups;
    }
    @GetMapping("/group/{id}/members")
    public List<UserDetailsDto> getGroupMembers(@PathVariable Integer id, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        return groupMembershipRepository.findGroupMembershipsByGroup(groupRepository.findGroupById(id).orElseThrow()).stream().map((gm)->new UserDetailsDto(gm.getMember().getUsername(),gm.getMember().getEmail(),gm.getMember().getNickname())).collect(Collectors.toList());
    }
    

    @PostMapping("/group/create")
        public ResponseEntity createGroup(@RequestBody GroupCreationDto groupCreationDto, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        groupService.createGroup(groupCreationDto, customUserDetails);
        System.out.println(groupCreationDto.groupName());
        System.out.println(groupCreationDto.users());
        return ResponseEntity.ok().body("Group created successfully");
    }
    @GetMapping("/group/balances/{id}")
    public List<BalanceDto> getGroupNetBalances(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Integer id){
        return netBalancesService.getGroupBalancesAsString(principal.getUser(),groupRepository.findGroupById(id).orElseThrow(),false);
    }
    @GetMapping("/group/balances/user/{id}")
    public List<BalanceDto> getGroupNetBalancesUser(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Integer id){
        return netBalancesService.getGroupBalancesAsString(principal.getUser(),groupRepository.findGroupById(id).orElseThrow(),true);
    }
}
