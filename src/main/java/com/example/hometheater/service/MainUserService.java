package com.example.hometheater.service;

import com.example.hometheater.config.SecurityConfig;
import com.example.hometheater.utils.DataAccessObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;


import com.example.hometheater.models.MainUser;

@Service
public class MainUserService {


    @Autowired
    private final DataAccessObject dataAccessObject;
    private final SecurityConfig securityConfig;

    public MainUserService(DataAccessObject dataAccessObject, SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
        this.dataAccessObject = dataAccessObject;
    }

    public String getAdminPassword(String username) throws SQLException {
        MainUser user = dataAccessObject.getMainUserByUsername(username);
        if (user == null) {
            return null;
        }
        return user.getPassword_hash(); // hashed password
    }

    public int getMainUserId() {
        return dataAccessObject.getMainUserId();
    }
    public MainUser getMainUser() {
        return dataAccessObject.getMainUsers();
    }

    public void changeUsername(String newUsername) throws SQLException {
        dataAccessObject.changeMainUsername(newUsername);
    }
    public void changePassword(String newPassword) throws SQLException {

        dataAccessObject.updateMainUserPassword(securityConfig.getPassword(newPassword), getMainUser());
    }
}

