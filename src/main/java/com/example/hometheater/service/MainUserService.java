package com.example.hometheater.service;

import com.example.hometheater.config.SecurityConfig;
import com.example.hometheater.repository.MainUserRepository;
import com.example.hometheater.repository.DAO.DataAccessObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;


import com.example.hometheater.models.MainUser;

@Service
public class MainUserService {


    @Autowired
    private final DataAccessObject dataAccessObject;
    private final SecurityConfig securityConfig;
    private final MainUserRepository mainUserRepository;
    public MainUserService(DataAccessObject dataAccessObject, SecurityConfig securityConfig,
                           MainUserRepository mainUserRepository) {
        this.securityConfig = securityConfig;
        this.dataAccessObject = dataAccessObject;
        this.mainUserRepository = mainUserRepository;
    }

    public String getAdminPassword(String username) throws SQLException {
//        MainUser user = dataAccessObject.getMainUserByUsername(username);
//        if (user == null) {
//            return null;
//        }
//        return user.getPassword_hash(); // hashed password
        return mainUserRepository.getAdminPassword(username);
    }

    public int getMainUserId() {
//        return dataAccessObject.getMainUserId();
        return mainUserRepository.getMainUserId();
    }

    public MainUser getMainUser() {
//        return dataAccessObject.getMainUsers();
        return mainUserRepository.getMainUser();
    }

    public void changeUsername(String newUsername) throws SQLException {
//        dataAccessObject.changeMainUsername(newUsername);
        mainUserRepository.changeUsername(newUsername);
    }
    public void changePassword(String newPassword) throws SQLException {

//        dataAccessObject.updateMainUserPassword(securityConfig.getPassword(newPassword), getMainUser());
        mainUserRepository.changePassword(newPassword);
    }
}

