-- Drop tables if they exist
DROP TABLE IF EXISTS unfinished_movies;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS main_user;

-- Create main_user table
CREATE TABLE main_user (
                           main_user_id INT AUTO_INCREMENT PRIMARY KEY,
                           username VARCHAR(255) NOT NULL,
                           password_hash VARCHAR(255) NOT NULL
);

-- Create users table with foreign key
CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       main_user_id INT NOT NULL,
                       username VARCHAR(255) NOT NULL,
                       profile_picture_path VARCHAR(255) NOT NULL,
                       CONSTRAINT fk_users_main_user FOREIGN KEY (main_user_id)
                           REFERENCES main_user(main_user_id) ON DELETE CASCADE
);

-- Create unfinished_movies table with composite primary key
CREATE TABLE unfinished_movies (
                                   user_id INT NOT NULL,
                                   video_path VARCHAR(255) NOT NULL,
                                   last_watched TIMESTAMP,
                                   watched_seconds INT,
                                   PRIMARY KEY(user_id, video_path),
                                   CONSTRAINT fk_unfinished_movies_users FOREIGN KEY (user_id)
                                       REFERENCES users(user_id) ON DELETE CASCADE
);

-- Insert sample data
INSERT INTO main_user (username, password_hash) VALUES ('testMain_User1', 'pswtest1');
INSERT INTO main_user (username, password_hash) VALUES ('testMain_User2', 'pswtest2');

INSERT INTO users (main_user_id, username, profile_picture_path) VALUES (1, 'testUser1', 'path/to/pic1');
INSERT INTO users (main_user_id, username, profile_picture_path) VALUES (2, 'testUser2', 'path/to/pic2');

-- H2 accepts 'YYYY-MM-DD HH:MM:SS' for TIMESTAMP
INSERT INTO unfinished_movies (user_id, video_path, last_watched, watched_seconds)
VALUES (1, 'path/to/video', '2025-06-10 00:00:00', 200);
