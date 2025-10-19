package com.example.hometheater.controller;


import com.example.hometheater.models.ProfileUser;
import com.example.hometheater.service.MainUserService;
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
    private final MainUserService mainUserService;
    public ProfileUserService profileUserService;
    public ProfilesController(ProfileUserService profileUserService, MainUserService mainUserService) {
        this.profileUserService = profileUserService;
        this.mainUserService = mainUserService;
    }


    @GetMapping("/")
    public String profile(Model model) throws SQLException {
        List<ProfileUser> users = profileUserService.getAllUsers();
        model.addAttribute("users", users);
        for (ProfileUser profileUser : users) {
            System.out.println("[SYSTEM] User found in endpoint / " + profileUser.getUsername() + "\n" + profileUser.getProfilePicturePath() + "\n user_id: " + profileUser.getUserId());

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
        System.out.println("[SYSTEM] Selected User: " + selectedUser.getUsername() + selectedUser.getUserId());

        if (selectedUser != null) {
            session.setAttribute("selectedUser", selectedUser);
        }
        return "redirect:/videos/browser";
    }
    @GetMapping("/profiles/manageAccount")
    public String manageAccountProfiles(Model model, HttpSession session) throws SQLException{
        model.addAttribute("users", profileUserService.getAllUsers());
        model.addAttribute("main_user_details", mainUserService.getMainUser());

        return "manage_account";
    }
    @GetMapping("/profiles/deleteUser")
    public String deleteUser(@RequestParam("userId") int userId ) {
        System.out.println("user delete user id " + userId);
        return "redirect:/profiles/manageAccount";
    }

    @PostMapping("/profiles/changePassword")
    public String changePassword(
            @RequestParam("username") String username,
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            Model model) {

        System.out.println("Changing password for " + username + " from " + currentPassword + " To " + newPassword);
        // 4. Show success message
        model.addAttribute("message", "Password successfully updated!");
        return "redirect:/profiles/manageAccount";
    }

    @PostMapping("/profiles/changeMainUserName")
    public String changeMainUserName(@RequestParam("newMainUserName") String newUserName) {
        System.out.println("new Username = " + newUserName);
        return "redirect:/profiles/manageAccount";

    }


}

