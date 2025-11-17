package com.example.hometheater.repository;


import com.example.hometheater.config.SecurityConfig;
import com.example.hometheater.models.MainUser;
import com.example.hometheater.repository.DAO.DataAccessObject;
import com.example.hometheater.repository.DAO.MainUserDAO;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;

@Repository
public class MainUserRepository {

    public DataAccessObject dataAccessObject;
    public MainUserDAO mainUserDAO;
    private final SecurityConfig securityConfig;
    public MainUserRepository(DataAccessObject dataAccessObject, MainUserDAO mainUserDAO, SecurityConfig securityConfig) {
        this.dataAccessObject = dataAccessObject;
        this.mainUserDAO = mainUserDAO;
        this.securityConfig = securityConfig;
    }

    public String getAdminPassword(String username) throws SQLException {
        MainUser user = mainUserDAO.getMainUserByUsername(username);
        if (user == null) {
            return null;
        }
        return user.getPassword_hash(); // hashed password
    }

    public int getMainUserId() {
        return mainUserDAO.getMainUserId();
    }
    public MainUser getMainUser() {
        return mainUserDAO.getMainUsers();
    }

    public void changeUsername(String newUsername) throws SQLException {
        mainUserDAO.changeMainUsername(newUsername);
    }
    public void changePassword(String newPassword) throws SQLException {

        mainUserDAO.updateMainUserPassword(securityConfig.getPassword(newPassword), getMainUser());
    }
}
