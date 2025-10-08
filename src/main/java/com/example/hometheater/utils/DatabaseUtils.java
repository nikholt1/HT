package com.example.hometheater.utils;

import com.example.hometheater.models.MainUser;
import com.example.hometheater.models.ProfileUser;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


import com.example.hometheater.models.ProfileUser;
import com.example.hometheater.models.MainUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DatabaseUtils {


    @Autowired
    private JdbcTemplate jdbcTemplate;



    public DatabaseUtils(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    // -----------------------------
    // MainUser methods
    // -----------------------------

    public MainUser getMainUsers() {
        String sql = "SELECT main_user_id, username, password_hash FROM main_user";

        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                MainUser user = new MainUser();
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password_hash"));
                return user;
            }
            return null;
        });
    }

    public boolean mainUserFirstInitializer() {
        String sql = "INSERT INTO main_user (username, password_hash) VALUES (?, ?)";

        try {
            int updated = jdbcTemplate.update(sql, "Admin", "admin");
            System.out.println("[SYSTEM] Insert successful via JDBC");
            return updated > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // -----------------------------
    // ProfileUser methods
    // -----------------------------

    public List<ProfileUser> getUsers() throws SQLException {
        String sql = "SELECT username, profile_picture_path FROM users";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ProfileUser user = new ProfileUser();
            user.setUsername(rs.getString("username"));
            user.setProfilePicturePath(rs.getString("profile_picture_path"));
            return user;
        });
    }

    public void addUser(ProfileUser profileuser) throws SQLException {
        String sql = "INSERT INTO users (username, profile_picture_path, main_user_id) VALUES (?, ?, ?)";

        jdbcTemplate.update(sql,
                profileuser.getUsername(),
                "images/profileImages/" + profileuser.getProfilePicturePath(),
                1 // example main_user_id
        );
    }

    public void deleteUser(String username) throws SQLException {
        String sql = "DELETE FROM users WHERE username = ?";
        jdbcTemplate.update(sql, username);
    }

}
