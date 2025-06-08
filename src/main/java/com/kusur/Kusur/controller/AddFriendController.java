package com.kusur.Kusur.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AddFriendController {

    @GetMapping("/add-friend")
    public String addFriend() {
        return "add-friend";
    }
}
