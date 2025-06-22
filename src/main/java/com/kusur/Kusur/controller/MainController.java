package com.kusur.Kusur.controller;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.kusur.Kusur.dto.UserDetailsDto;
import com.kusur.Kusur.model.Friendship;
import com.kusur.Kusur.model.FriendshipStatus;
import com.kusur.Kusur.model.User;
import com.kusur.Kusur.repository.FriendshipRepository;
import com.kusur.Kusur.repository.UserRepository;
import com.kusur.Kusur.security.CustomUserDetails;
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
    @Autowired
    FriendshipRepository  friendshipRepository;
    @Autowired
    UserRepository userRepository;
    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }
    @GetMapping("/home")
    public String home(Model model,@AuthenticationPrincipal CustomUserDetails principal) {
        List<User> f1 = new ArrayList<>(friendshipRepository.findFriendshipsBySender(principal.getUser()).stream().map(f -> f.getReceiver()).toList());
        List<User> f2 = friendshipRepository.findFriendshipsByReceiver(principal.getUser()).stream().map( f -> f.getSender()).toList();
        f1.addAll(f2);

        System.out.println(f1);
        model.addAttribute("friends", f1);
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
}
