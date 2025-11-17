package com.example.hometheater.repository;

import com.example.hometheater.models.ProfileUser;
import com.example.hometheater.repository.DAO.DataAccessObject;
import com.example.hometheater.repository.DAO.UserDAO;
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

    private final String filePath = "src/main/java/com/example/hometheater/repository/settings.conf";


    public String getUserName() {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                if (line.startsWith("userName=")) {
                    String value = line.substring("userName=".length()).trim();
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    return value;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
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
