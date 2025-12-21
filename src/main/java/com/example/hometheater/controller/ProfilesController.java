package com.example.hometheater.controller;


import com.example.hometheater.models.MainUser;
import com.example.hometheater.models.ProfileUser;
import com.example.hometheater.service.MainUserService;
import com.example.hometheater.service.ProfileUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.hometheater.config.SecurityConfig;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.sql.SQLException;
import java.util.List;


@Controller
public class ProfilesController {
    private final MainUserService mainUserService;
    public ProfileUserService profileUserService;
    private final SecurityConfig securityConfig;
    public ProfilesController(ProfileUserService profileUserService, MainUserService mainUserService, SecurityConfig securityConfig) {
        this.profileUserService = profileUserService;
        this.mainUserService = mainUserService;
        this.securityConfig = securityConfig;
    }


    @GetMapping("/")
    public String profile(Model model) throws SQLException {
        List<ProfileUser> users = profileUserService.getAllUsers();
        model.addAttribute("users", users);
        for (ProfileUser profileUser : users) {
            System.out.println("[SYSTEM] User found in endpoint / " + profileUser.getUsername() + "\n" + profileUser.getProfilePicturePath() + "\n user_id: " + profileUser.getUserId());

        }
        return "profiles";
    }

    @GetMapping("/profiles/add")
    public String profileAdd(Model model) throws SQLException {
        model.addAttribute("user", new ProfileUser());

        File folder = new File("../data/profileImages");
        String[] images = folder.list((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png"));


        if (images != null) {
            for (String image : images) {
                System.out.println("Found image: " + folder.getAbsolutePath() + "/" + image);
            }
        } else {
            System.out.println("No images found in folder: " + folder.getAbsolutePath());
        }

        model.addAttribute("images", images);
        return "profile_add";
    }



    @PostMapping("/profiles/addUser")
    public String addProfile(@ModelAttribute ProfileUser user) throws SQLException {
        profileUserService.addUser(user);
        return "redirect:/";
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
//        model.addAttribute("backgroundURL", manage_accounts_background_url);
        return "manage_account";
    }
    @GetMapping("/profiles/deleteUser/{username}")
    public String deleteUser(@PathVariable String username ) {
        System.out.println("[SYSTEM] delete user hit " + username);
        try {
            profileUserService.deleteUser(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "redirect:/profiles/manageAccount";
    }

    @PostMapping("/profiles/changePassword")
    public String changePassword(
            @RequestParam("username") String username,
            @RequestParam("newPassword") String newPassword,
            RedirectAttributes redirectAttributes) { // <-- use RedirectAttributes
        MainUser mainUser = mainUserService.getMainUser();

        try {
            mainUserService.changePassword(newPassword);
            redirectAttributes.addFlashAttribute("message", "Password successfully updated!");
        } catch (SQLException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Error updating password, try something different");
        }

        return "redirect:/profiles/manageAccount";
    }

    @PostMapping("/profiles/changeMainUserName")
    public String changeMainUserName(@RequestParam("newMainUserName") String newUserName,
                                     RedirectAttributes redirectAttributes) {
        try {
            mainUserService.changeUsername(newUserName);
            redirectAttributes.addFlashAttribute("Usernamemessage", "Username successfully updated!");
        } catch (SQLException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("Usernamemessage", "Error updating username, try something different");
        }
        return "redirect:/profiles/manageAccount";
    }



}

