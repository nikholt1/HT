package com.example.hometheater;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private static final String DB_PATH = "database/hometheaterdb.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;

    @Override
    public void run(String... args) throws Exception {

        File dbFile = new File(DB_PATH);
        boolean dbExists = dbFile.exists();

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                System.out.println(dbExists
                        ? "SQLite database already exists: " + DB_PATH
                        : "SQLite database did not exist and was created: " + DB_PATH);

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON;");

                    checkAndCreateTable(conn, stmt, "main_user",
                            """
                            CREATE TABLE main_user (
                                main_user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                username TEXT NOT NULL,
                                password_hash TEXT NOT NULL
                            );
                            """);

                    checkAndCreateTable(conn, stmt, "users",
                            """
                            CREATE TABLE users (
                                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                main_user_id INTEGER NOT NULL,
                                username TEXT NOT NULL,
                                profile_picture_path TEXT NOT NULL,
                                FOREIGN KEY(main_user_id) REFERENCES main_user(main_user_id) ON DELETE CASCADE
                            );
                            """);

                    checkAndCreateTable(conn, stmt, "unfinished_movies",
                            """
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
                System.out.println("Table already exists: " + tableName);
            } else {
                stmt.executeUpdate(createSQL);
                System.out.println("Table created: " + tableName);
            }
        }
    }
}


