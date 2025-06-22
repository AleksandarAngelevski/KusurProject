package com.kusur.Kusur.controller;

import com.kusur.Kusur.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
public class LoginController {

    @RequestMapping("/login")
    public String login(Principal principal) {  
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getName());

        if(principal != null){
            return "redirect:/home";
        }
        return "login";
    }
}
