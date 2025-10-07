package com.example.hometheater.service;

import com.example.hometheater.models.ProfileUser;
import com.example.hometheater.utils.DatabaseUtils;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class ProfileUserService {
    public DatabaseUtils databaseUtils;
    public ProfileUserService(DatabaseUtils databaseUtils) {
        this.databaseUtils = databaseUtils;
    }

    public void addUser(ProfileUser profilerUser) {
        try {
            databaseUtils.addUser(profilerUser);
        } catch (SQLException ex) {
            System.out.println("Error adding user");
        }

    }

    public List<ProfileUser> getAllUsers() throws SQLException {
        return databaseUtils.getUsers();
    }
}
