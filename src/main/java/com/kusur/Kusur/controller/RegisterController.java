package com.kusur.Kusur.controller;


import com.kusur.Kusur.dto.UserRegistrationDto;
import com.kusur.Kusur.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller

public class RegisterController {
    @Autowired
    public CustomUserDetailsService customUserDetailsService;

    public RegisterController(CustomUserDetailsService userDetailsService) {
        this.customUserDetailsService = userDetailsService;
    }


    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationDTO", new UserRegistrationDto());
        return "register";
    }
    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserRegistrationDto registrationDTO, Model model) {
        try{
            System.out.println("Registering user");
            customUserDetailsService.registerNewUser(registrationDTO);
            return "redirect:/login?registered";
        }catch (Exception e){
            model.addAttribute("registrationDTO", new UserRegistrationDto());
            model.addAttribute("error",e.getMessage());
            return "register";
        }
    }



}
