package com.example.hometheater.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MainUserService mainUserService;

    public CustomUserDetailsService(MainUserService mainUserService) {
        this.mainUserService = mainUserService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("[SYSTEM] main_user_id = " + mainUserService.getMainUserId());
        try {
            String hashedPassword = mainUserService.getAdminPassword(username);
            if (hashedPassword == null) {
                throw new UsernameNotFoundException("[SYSTEM] User not found: " + username);
            }

            return User.withUsername(username)
                    .password(hashedPassword)
                    .roles("ADMIN")
                    .build();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

