package com.kusur.Kusur.controller;


import com.kusur.Kusur.dto.UserRegistrationDto;
import com.kusur.Kusur.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller

public class RegisterController {
    @Autowired
    public CustomUserDetailsService customUserDetailsService;

    public RegisterController(CustomUserDetailsService userDetailsService) {
        this.customUserDetailsService = userDetailsService;
    }


    @GetMapping("/register")
    public String showRegistrationForm(Model model, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(principal != null){
            return "redirect:/home";
        }
        model.addAttribute("registrationDTO", new UserRegistrationDto());
        return "register";
    }
    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserRegistrationDto registrationDTO, Model model, RedirectAttributes redirectAttributes) {
        try{
            System.out.println("Registering user");
            customUserDetailsService.registerNewUser(registrationDTO);
            redirectAttributes.addAttribute("email",registrationDTO.email());
            return "redirect:/checkEmail";
        }catch (Exception e){
            model.addAttribute("registrationDTO", new UserRegistrationDto());
            model.addAttribute("error",e.getMessage());
            return "register";
        }
    }



}
