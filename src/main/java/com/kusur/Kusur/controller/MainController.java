package com.kusur.Kusur.controller;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.kusur.Kusur.dto.BalanceDto;
import com.kusur.Kusur.dto.UserDetailsDto;
import com.kusur.Kusur.model.*;
import com.kusur.Kusur.repository.*;
import com.kusur.Kusur.security.CustomUserDetails;
import com.kusur.Kusur.service.GroupService;
import com.kusur.Kusur.service.NetBalanceCalculatorService;
import com.kusur.Kusur.service.SplitExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.thymeleaf.util.ListUtils;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MainController {
    GroupService groupService;
    @Autowired
    FriendshipRepository  friendshipRepository;
    @Autowired
    GroupRepository     groupRepository;
    @Autowired
    GroupMembershipRepository groupMembershipRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SplitExpenseService splitExpenseService;
    @Autowired
    UserNetBalancesRepository userNetBalancesRepository;
    @Autowired
    NetBalanceCalculatorService netBalanceCalculatorService;
    @Autowired
    ExpenseRepository expenseRepository;
    @Autowired
    MainController(GroupService groupService,GroupRepository groupRepository){
        this.groupService = groupService;
        this.groupRepository = groupRepository;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }
    @GetMapping("/home")
    public String home(Model model,@AuthenticationPrincipal CustomUserDetails principal) {
        List<User> f1 = new ArrayList<>(friendshipRepository.findFriendshipsBySender(principal.getUser()).stream().map(f -> f.getReceiver()).toList());
        List<User> f2 = friendshipRepository.findFriendshipsByReceiver(principal.getUser()).stream().map( f -> f.getSender()).toList();
        f1.addAll(f2);
        List<Expense> expenses = splitExpenseService.getAllExpenses(principal.getUser());
        System.out.println(groupMembershipRepository.findGroupMembershipByMember(principal.getUser()));
        List<Group> groups = new ArrayList<>(groupMembershipRepository.findGroupMembershipByMember(principal.getUser()).stream().map(m -> m.getGroup()).collect(Collectors.toList()));

        List<String> userNetBalances = netBalanceCalculatorService.getAllUserBalancesAsString(principal.getUser());
        List<String> groupNetBalances = netBalanceCalculatorService.getAllGroupBalancesAsString(principal.getUser());
        System.out.println(userNetBalances);
        model.addAttribute("userBalances",userNetBalances);
        model.addAttribute("groups",groups);
        model.addAttribute("friends", f1);
        model.addAttribute("expenses",expenses);
        System.out.println(expenses);
        return "home.html";
    }
    @GetMapping("/account")
    public String account(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        System.out.println(userDetails.getNickname());
        UserDetailsDto detailsDto = new UserDetailsDto(userDetails.getUsername(),userDetails.getEmail(),userDetails.getNickname());
        model.addAttribute("user_details",detailsDto);
        return "account.html";
    }
    @GetMapping("/friends/{username}")
    public String friends(Model model, @PathVariable String username){
        try {
            User u = userRepository.findByUsername(username).orElseThrow(() -> new Exception("User not found"));
            UserDetailsDto detailsDto = new UserDetailsDto(u.getUsername(),u.getEmail(),u.getNickname());
            model.addAttribute("user_details",detailsDto);
            return "account.html";
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return "account.html";

    }
    @GetMapping("/groups/{id}")
    public String group(Model model ,@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Integer id){
        Group groupObj = groupRepository.findGroupById(id).orElseThrow();
        model.addAttribute("group",groupObj);
        List<UserDetailsDto> users = groupService.getGroupMembers(groupRepository.findGroupById(id).orElseThrow());
        List<Expense> expenses = splitExpenseService.getGroupExpenses(principal.getUser(),groupObj);
        List<BalanceDto> groupNetBalances =netBalanceCalculatorService.getGroupBalancesAsString(principal.getUser(),groupObj,false);
        System.out.println(groupNetBalances +"\n GroupNetBalances");
        model.addAttribute("users",users);
        model.addAttribute("expenses",expenses);
        model.addAttribute("groupBalances",groupNetBalances);

        return "group";
    }

}
