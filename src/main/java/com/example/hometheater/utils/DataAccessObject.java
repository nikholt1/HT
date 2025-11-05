package com.example.hometheater.utils;

import com.example.hometheater.config.SecurityConfig;
import com.example.hometheater.models.MainUser;
import com.example.hometheater.models.ProfileUser;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;

@Repository
public class DataAccessObject {


    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private JdbcTemplate jdbcTemplate;



    public DataAccessObject(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }


    public MainUser getMainUsers() {
        String sql = "SELECT main_user_id, username, password_hash FROM main_user";

        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                MainUser user = new MainUser();
                user.setUsername(rs.getString("username"));
                user.setPassword_hash(rs.getString("password_hash"));
                return user;
            }
            return null;
        });
    }

    public void mainUserFirstInitializer() {

        String sqlInsertNewMainUser = "INSERT INTO main_user (username, password_hash) VALUES (?, ?)";

        String sqlFetchMainUserTableValues =
                "SELECT COUNT(*) "
                        + "FROM main_user";

        Integer result = jdbcTemplate.queryForObject(sqlFetchMainUserTableValues, Integer.class);
        System.out.println("[SYSTEM] Found " + result + " rows in table main_user (Breaking Init default main_user creation)");

        if (result == 0) {
            try {
                String encodedPsw = securityConfig.getPassword("admin");

                int updated = jdbcTemplate.update(sqlInsertNewMainUser, "Admin", encodedPsw);

                if (updated == 1) {
                    System.out.println("[SYSTEM] Insert of default main user successful via JDBC");
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    public void defaultSubUserInitializer() {
        String sqlInsertNewMainUser =
                "INSERT INTO users (main_user_id, username, profile_picture_path) VALUES (?, ?, ?)";

        String sqlFetchMainUserTableValues =
                "SELECT COUNT(*) "
                        + "FROM users";

        Integer result = jdbcTemplate.queryForObject(sqlFetchMainUserTableValues, Integer.class);
        System.out.println("[SYSTEM] Found " + result + " rows in table users (Breaking Init default user creation)");

        if (result == 0) {
            try {


                int updated = jdbcTemplate.update(sqlInsertNewMainUser, 1, "DefaultUser", "/images/profileImages/GirlCatProf.jpg");

                if (updated == 1) {
                    System.out.println("[SYSTEM] Insert of default user successful via JDBC");
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }



//    public boolean mainUserFirstInitializer() {
//        String sql = "INSERT INTO main_user (username, password_hash) VALUES (?, ?)";
//
//
//        String sqlCheckForUser = "SELECT main_user_id FROM main_user";
//
//        boolean userThereCheck = false;
//
//        try {
//            // Check if there is already a user
//            Integer count = jdbcTemplate.queryForObject(sqlCheckForUser, Integer.class);
//            if (count != null && count > 0) {
//                userThereCheck = true;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false; // DB error
//        }
//
//        if (!userThereCheck) {
//            try {
//                String encodedPsw = securityConfig.getPassword("admin");
//
//                int updated = jdbcTemplate.update(sql, "Admin", encodedPsw);
//                System.out.println("[SYSTEM] Insert successful via JDBC");
//                return updated > 0;
//            } catch (Exception e) {
//                e.printStackTrace();
//                return false;
//            }
//        }
//        return false;
//
//    }

    // -----------------------------
    // ProfileUser methods
    // -----------------------------

    public List<ProfileUser> getUsers() throws SQLException {
        String sql = "SELECT user_id, username, profile_picture_path FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ProfileUser user = new ProfileUser();
            user.setUserId(rs.getInt("user_id"));
            user.setUsername(rs.getString("username"));
            user.setProfilePicturePath(rs.getString("profile_picture_path"));
            return user;
        });

    }

    public void addUser(ProfileUser profileuser) throws SQLException {
        String sql = "INSERT INTO users (username, profile_picture_path, main_user_id) VALUES (?, ?, ?)";

        jdbcTemplate.update(sql,
                profileuser.getUsername(),
                profileuser.getProfilePicturePath(),
                1
        );
    }

    public void deleteUser(String username) throws SQLException {
        String sql = "DELETE FROM users WHERE username = ?";
        jdbcTemplate.update(sql, username);
    }
    public MainUser getMainUserByUsername(String username) {
        String sql = "SELECT username, password_hash FROM main_user WHERE username = ?";

        try {
            MainUser mainUser = jdbcTemplate.queryForObject(
                    sql,
                    new Object[]{username},
                    new org.springframework.jdbc.core.BeanPropertyRowMapper<>(MainUser.class)
            );
            System.out.println(mainUser.getUsername() + " " + mainUser.getPassword_hash());
            return mainUser;
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void updateUserUsername(int userId, String newUsername) throws SQLException {
        System.out.println("[SYSTEM] in DAO updateUserUserName with user_id " + userId + " To " + newUsername);
        String sql = "UPDATE users SET username = ? WHERE user_id = ?";

        jdbcTemplate.update(sql, newUsername, userId);

    }



    public Integer getMainUserId() {
        String sql = "SELECT main_user_id FROM main_user";
        int main_user_id = jdbcTemplate.queryForObject(sql, Integer.class);
        return main_user_id;
    }

    public void updateMainUserPassword() {
        String username = getMainUsers().getUsername();
        String rawPassword = "admin";
        String hashedPassword = securityConfig.getPassword(rawPassword);

        String sql = "UPDATE main_user SET password_hash = ? WHERE username = ?";

        // Use DatabaseUtils JdbcTemplate for the update
        int rowsUpdated = jdbcTemplate.update(sql, hashedPassword, username);

        if (rowsUpdated > 0) {
            System.out.println("Password updated successfully for user: " + username);
        } else {
            System.out.println("User not found: " + username);
        }
    }

    public String getMainUserPassword() {
        MainUser mainUser = getMainUsers();
        String username = mainUser.getUsername();
        if (mainUser != null && mainUser.getUsername().equals(username)) {
            return mainUser.getPassword_hash();
        }
        return null;
    }
}
