-- Insert sample data
INSERT INTO main_user (username, password_hash) VALUES ('testMain_User1', 'pswtest1');
INSERT INTO main_user (username, password_hash) VALUES ('testMain_User2', 'pswtest2');

INSERT INTO users (main_user_id, username, profile_picture_path) VALUES (1, 'testUser1', 'path/to/pic1');
INSERT INTO users (main_user_id, username, profile_picture_path) VALUES (2, 'testUser2', 'path/to/pic2');

-- H2 accepts 'YYYY-MM-DD HH:MM:SS' for TIMESTAMP
INSERT INTO unfinished_movies (user_id, video_path, last_watched, watched_seconds)
VALUES (1, 'path/to/video', '2025-06-10 00:00:00', 200);
