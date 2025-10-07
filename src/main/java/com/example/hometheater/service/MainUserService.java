package com.example.hometheater.service;

import com.example.hometheater.utils.DatabaseUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import com.example.hometheater.models.MainUser;
import com.example.hometheater.utils.DatabaseUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.hometheater.models.MainUser;
import com.example.hometheater.utils.DatabaseUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.SQLException;

@Service
public class MainUserService {

    private final PasswordEncoder passwordEncoder;
    private final DatabaseUtils databaseUtils;

    public MainUserService(PasswordEncoder passwordEncoder, DatabaseUtils databaseUtils) {
        this.passwordEncoder = passwordEncoder;
        this.databaseUtils = databaseUtils;
    }

    // Get the password hash for a given username
    public String getAdminPassword(String username) throws SQLException {
        // Use DatabaseUtils instead of manual JDBC
        MainUser mainUser = databaseUtils.getMainUsers();
        if (mainUser != null && mainUser.getUsername().equals(username)) {
            return mainUser.getPassword();
        }
        return null;
    }

    // Update the admin password
    public void updatePassword() throws SQLException {
        String username = "Admin";
        String rawPassword = "admin";
        String hashedPassword = passwordEncoder.encode(rawPassword);

        String sql = "UPDATE main_user SET password_hash = ? WHERE username = ?";

        // Use DatabaseUtils JdbcTemplate for the update
        int rowsUpdated = databaseUtils.getJdbcTemplate().update(sql, hashedPassword, username);

        if (rowsUpdated > 0) {
            System.out.println("Password updated successfully for user: " + username);
        } else {
            System.out.println("User not found: " + username);
        }
    }
}

