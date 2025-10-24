package com.example.hometheater.service;

import com.example.hometheater.models.ProfileUser;
import com.example.hometheater.utils.DataAccessObject;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class ProfileUserService {
    public DataAccessObject dataAccessObject;
    public ProfileUserService(DataAccessObject dataAccessObject) {
        this.dataAccessObject = dataAccessObject;
    }

    public void addUser(ProfileUser profilerUser) {
        try {
            dataAccessObject.addUser(profilerUser);
        } catch (SQLException ex) {
            System.out.println("Error adding user");
        }

    }

    public List<ProfileUser> getAllUsers() throws SQLException {
        return dataAccessObject.getUsers();
    }
}
