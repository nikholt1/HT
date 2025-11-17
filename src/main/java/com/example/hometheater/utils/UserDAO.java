package com.example.hometheater.utils;

import com.example.hometheater.config.SecurityConfig;
import com.example.hometheater.models.ProfileUser;
import com.example.hometheater.service.MainUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;


@Repository
public class UserDAO {

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private MainUserService mainUserService;


    public UserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
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
}
