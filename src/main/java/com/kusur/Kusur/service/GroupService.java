package com.kusur.Kusur.service;

import com.kusur.Kusur.dto.GroupCreationDto;
import com.kusur.Kusur.dto.UserDetailsDto;
import com.kusur.Kusur.model.Group;
import com.kusur.Kusur.model.GroupMembership;
import com.kusur.Kusur.model.User;
import com.kusur.Kusur.repository.GroupMembershipRepository;
import com.kusur.Kusur.repository.GroupRepository;
import com.kusur.Kusur.repository.UserRepository;
import com.kusur.Kusur.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class GroupService {
    @Autowired
    private GroupMembershipRepository groupMembershipRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;

    public Group createGroup(GroupCreationDto groupCreationDto, @AuthenticationPrincipal CustomUserDetails principal){
        List<User> users = groupCreationDto.users().stream().map((s)->userRepository.findByUsername(s).orElseThrow()).toList();
        Group gr =new Group(groupCreationDto.groupName(),principal.getUser());
        groupRepository.save(gr);
        groupMembershipRepository.save(new GroupMembership(gr,principal.getUser()));
        users.forEach(user -> groupMembershipRepository.save(new GroupMembership(gr,user)));

        System.out.println("Group created");
        return gr;
    }
    public List<UserDetailsDto> getGroupMembers(Group group){
        List<UserDetailsDto> members =groupMembershipRepository.findGroupMembershipsByGroup(group).stream().map((gm) -> gm.getMember()).toList().stream().map((u)-> new UserDetailsDto(u.getUsername(),u.getEmail(),u.getNickname())).collect(Collectors.toList());
        return members;
    }
}
