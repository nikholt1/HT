package com.example.hometheater.repository;

import com.example.hometheater.models.ProfileUser;
import com.example.hometheater.utils.DataAccessObject;
import com.example.hometheater.utils.UserDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

@Repository
public class UserRepository {
    public DataAccessObject dataAccessObject;
    public UserDAO userDAO;
    public UserRepository(DataAccessObject dataAccessObject, UserDAO userDAO) {
        this.dataAccessObject = dataAccessObject;
        this.userDAO = userDAO;
    }

    @Value("${app.folder-path}")
    private String folderPath;


    public String getUserName() {
        return "test";
    }

    public void addUser(ProfileUser profilerUser) {
        try {
            userDAO.addUser(profilerUser);
        } catch (SQLException ex) {
            System.out.println("Error adding user");
        }

    }
    public void deleteUser(String username) throws SQLException{
        userDAO.deleteUser(username);
    }
    public List<ProfileUser> getAllUsers() throws SQLException {
        return userDAO.getUsers();
    }
}
