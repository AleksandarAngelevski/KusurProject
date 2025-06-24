package com.kusur.Kusur.controller;

import com.kusur.Kusur.dto.UserDetailsDto;
import com.kusur.Kusur.model.User;
import com.kusur.Kusur.repository.FriendshipRepository;
import com.kusur.Kusur.repository.UserRepository;
import com.kusur.Kusur.security.CustomUserDetails;
import com.kusur.Kusur.service.AddFriendService;
import com.kusur.Kusur.service.CustomUserDetailsService;
import org.eclipse.angus.mail.iap.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class AddFriendController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private AddFriendService addFriendService;

    @PostMapping("/add-friend")
    public ResponseEntity<String> addFriend(@RequestBody friendRequestDTO model,Principal principal) {
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAA");
        System.out.println(model.username());
        String receiverUsername = model.username();
        try{
            addFriendService.sendFriendRequest(principal.getName(),receiverUsername);
            return ResponseEntity.ok("Friend request sent");
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }
    @GetMapping("/get-friends")
    public ResponseEntity<List<UserDetailsDto>> getFriends(@AuthenticationPrincipal CustomUserDetails principal) {
        User currentUser = principal.getUser();
        return  ResponseEntity.ok().body(addFriendService.getAllFriends(currentUser));
    }
}
record friendRequestDTO(String username) {};