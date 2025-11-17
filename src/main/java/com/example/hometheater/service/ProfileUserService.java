package com.example.hometheater.service;

import com.example.hometheater.models.ProfileUser;
import com.example.hometheater.repository.UserRepository;
import com.example.hometheater.utils.DataAccessObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class ProfileUserService {
    public DataAccessObject dataAccessObject;
    public UserRepository userRepository;
    public ProfileUserService(DataAccessObject dataAccessObject, UserRepository userRepository) {
        this.dataAccessObject = dataAccessObject;
        this.userRepository = userRepository;
    }

    public void addUser(ProfileUser profilerUser) {
//        try {
//            dataAccessObject.addUser(profilerUser);
//        } catch (SQLException ex) {
//            System.out.println("Error adding user");
//        }
        userRepository.addUser(profilerUser);

    }
    public void deleteUser(String username) throws SQLException{
//        dataAccessObject.deleteUser(username);
        userRepository.deleteUser(username);
    }
    public List<ProfileUser> getAllUsers() throws SQLException {
//        return dataAccessObject.getUsers();
        return userRepository.getAllUsers();
    }
}
