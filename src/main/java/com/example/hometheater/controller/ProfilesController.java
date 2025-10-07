package com.example.hometheater.controller;


import com.example.hometheater.models.ProfileUser;
import com.example.hometheater.service.ProfileUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.sql.SQLException;
import java.util.List;


@Controller
public class ProfilesController {
    public ProfileUserService profileUserService;
    public ProfilesController(ProfileUserService profileUserService) {
        this.profileUserService = profileUserService;
    }


    @GetMapping("/")
    public String profile(Model model) throws SQLException {
        List<ProfileUser> users = profileUserService.getAllUsers();
        model.addAttribute("users", users);
        for (ProfileUser profileUser : users) {
            System.out.println(profileUser.getProfilePicturePath());
            System.out.println(profileUser.getUsername());
            System.out.println(profileUser.getUserId());
        }
        return "profiles"; // Thymeleaf template: profile.html
    }

    @GetMapping("/profiles/add")
    public String profileAdd(Model model) throws SQLException {
        model.addAttribute("user", new ProfileUser());

        // List all images in static folder
        File folder = new File("src/main/resources/static/images/profileImages");
        String[] images = folder.list((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png"));
        model.addAttribute("images", images);
        return "profile_add";
    }


    @PostMapping("/profiles/addUser")
    public String addProfile(@ModelAttribute ProfileUser user) throws SQLException {
        profileUserService.addUser(user); // Save user in DB
        return "redirect:/"; // redirect to profile selection page
    }

    @GetMapping("/profiles/select")
    public String selectProfile(@RequestParam Long userId, HttpSession session) throws SQLException {
//        ProfileUser selectedUser = profileUserService.getUserById(userId);
        ProfileUser selectedUser = null;
        List<ProfileUser> users = profileUserService.getAllUsers();
        for (ProfileUser profileUser : users) {
            if  (profileUser.getUserId() == userId) {
                selectedUser = profileUser;
            }
        }
        System.out.println("Selected User: " + selectedUser.getUsername() + selectedUser.getUserId());

        if (selectedUser != null) {
            session.setAttribute("selectedUser", selectedUser);
        }
        return "redirect:/videos/browser";
    }

}

