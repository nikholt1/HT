package com.example.hometheater;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

@Component
@Profile("!test")
public class DatabaseInitializer implements CommandLineRunner {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Override
    public void run(String... args) {

        String dbPath = dbUrl.replace("jdbc:sqlite:", "");

        File dbFile = new File(dbPath);
        boolean dbExists = dbFile.exists();


        if (!dbFile.getParentFile().exists()) {
            dbFile.getParentFile().mkdirs();
        }

        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            if (conn != null) {
                System.out.println(dbExists
                        ? "[SYSTEM] SQLite database already exists: " + dbPath
                        : "[SYSTEM] SQLite database did not exist and was created: " + dbPath);

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON;");

                    checkAndCreateTable(conn, stmt, "main_user", """
                        CREATE TABLE main_user (
                            main_user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            username TEXT NOT NULL,
                            password_hash TEXT NOT NULL
                        );
                    """);

                    checkAndCreateTable(conn, stmt, "users", """
                        CREATE TABLE users (
                            user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            main_user_id INTEGER NOT NULL,
                            username TEXT NOT NULL,
                            profile_picture_path TEXT NOT NULL,
                            FOREIGN KEY(main_user_id) REFERENCES main_user(main_user_id) ON DELETE CASCADE
                        );
                    """);

                    checkAndCreateTable(conn, stmt, "unfinished_movies", """
                        CREATE TABLE unfinished_movies (
                            user_id INTEGER NOT NULL,
                            video_path TEXT NOT NULL,
                            last_watched TIMESTAMP,
                            watched_seconds INTEGER,
                            PRIMARY KEY(user_id, video_path),
                            FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE
                        );
                    """);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkAndCreateTable(Connection conn, Statement stmt, String tableName, String createSQL) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, tableName, new String[]{"TABLE"})) {
            if (rs.next()) {
                System.out.println("[SYSTEM] Table already exists: " + tableName);
            } else {
                stmt.executeUpdate(createSQL);
                System.out.println("[SYSTEM] Table created: " + tableName);
            }
        }
    }
}
