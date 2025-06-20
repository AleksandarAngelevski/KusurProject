package com.kusur.Kusur.controller;

import com.kusur.Kusur.dto.UserDetailsDto;
import com.kusur.Kusur.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class MainController {
    @GetMapping("/")
    public String index() {
        return "home.html";
    }
    @GetMapping("/home")
    public String home() {
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
}
