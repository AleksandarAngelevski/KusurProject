package com.kusur.Kusur.controller;

import com.kusur.Kusur.model.User;
import com.kusur.Kusur.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class VerificationController {
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/checkEmail")
    public String checkEmail(@RequestParam String email, Model model){
        model.addAttribute("email",email);
        System.out.println(email);
        return "checkEmail";
    }

    @GetMapping("/verify")
    public String verifyUser(@RequestParam("token")  String token, Model model){
        Optional<User> user = userRepository.findByVerificationToken(token);
        if(user.isEmpty()){
            model.addAttribute("error", "Invalid verification token");
            return "verify-fail";
        }

        User userMain = user.get();
        userMain.setEnabled(true);
        userMain.setVerificationToken(null);
        userRepository.save(userMain);
        model.addAttribute("message", "Account verified successfully! You can log in now");
        return "verify-success";
    }
}
