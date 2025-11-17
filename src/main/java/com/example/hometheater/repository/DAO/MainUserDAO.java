package com.example.hometheater.repository.DAO;

import com.example.hometheater.config.SecurityConfig;
import com.example.hometheater.models.MainUser;
import com.example.hometheater.service.MainUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MainUserDAO {
    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private JdbcTemplate jdbcTemplate;



    public MainUserDAO(JdbcTemplate jdbcTemplate) {
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

    public Integer getMainUserId() {
        String sql = "SELECT main_user_id FROM main_user";
        int main_user_id = jdbcTemplate.queryForObject(sql, Integer.class);
        return main_user_id;
    }

    public void updateMainUserPassword(String newPassword, MainUser mainUser) {

        String sql = "UPDATE main_user SET password_hash = ? WHERE main_user_id = ?";

        int rowsUpdated = jdbcTemplate.update(sql, newPassword, 1);

        if (rowsUpdated > 0) {
            System.out.println("Password updated successfully for user ID: " + 1);
        } else {
            System.out.println("User not found with ID: " + 1);
        }
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
    public void changeMainUsername(String newUsername) {
        String sql = "UPDATE main_user SET username = ? WHERE main_user_id = 1";
        jdbcTemplate.update(sql, newUsername);
    }
}
