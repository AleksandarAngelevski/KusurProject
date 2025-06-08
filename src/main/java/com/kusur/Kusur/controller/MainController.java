package com.kusur.Kusur.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
}
