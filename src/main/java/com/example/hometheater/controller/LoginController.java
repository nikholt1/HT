package com.example.hometheater.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

//    @Value("${login.background.url}")
//    private String backgroundUrl;

    @GetMapping("/login")
    public String loginPage(Model model) {
//        model.addAttribute("backgroundImageUrl", backgroundUrl);
        return "login";
    }
}

