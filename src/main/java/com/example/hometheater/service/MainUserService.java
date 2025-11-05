package com.example.hometheater.service;

import com.example.hometheater.utils.DataAccessObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;


import com.example.hometheater.models.MainUser;

@Service
public class MainUserService {


    @Autowired
    private final DataAccessObject dataAccessObject;

    public MainUserService(DataAccessObject dataAccessObject) {
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


    public void updatePassword() throws SQLException {
        dataAccessObject.updateMainUserPassword();
    }
}

