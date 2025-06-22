package com.kusur.Kusur.controller;

import com.kusur.Kusur.service.AddFriendService;
import org.eclipse.angus.mail.iap.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

@Controller
public class AddFriendController {
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
}
record friendRequestDTO(String username) {};