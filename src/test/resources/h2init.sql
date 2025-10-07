DROP TABLE IF EXISTS "main_user";
DROP TABLE IF EXISTS "users";
DROP TABLE IF EXISTS "unfinished_movies";

CREATE TABLE "main_user" (
                             "main_user_id" INTEGER PRIMARY KEY AUTO_INCREMENT,
                             "username" TEXT NOT NULL,
                             "password_hash" TEXT NOT NULL
);

CREATE TABLE "users" (
                         "user_id" INTEGER PRIMARY KEY AUTO_INCREMENT,
                         "main_user_id" INTEGER NOT NULL,
                         "username" TEXT NOT NULL,
                         "profile_picture_path" TEXT NOT NULL,
                         FOREIGN KEY("main_user_id") REFERENCES "main_user"("main_user_id") ON DELETE CASCADE
);

CREATE TABLE "unfinished_movies" (
                                     "user_id" INTEGER NOT NULL,
                                     "video_path" TEXT NOT NULL,
                                     "last_watched" TIMESTAMP,
                                     "watched_seconds" INTEGER,
                                     PRIMARY KEY("user_id", "video_path"),
                                     FOREIGN KEY("user_id") REFERENCES "users"("user_id") ON DELETE CASCADE
);

INSERT INTO "main_user" ("username", "password_hash") VALUES ('testMain_User1', 'pswtest1');
INSERT INTO "main_user" ("username", "password_hash") VALUES ('testMain_User2', 'pswtest2');

INSERT INTO "users" ("main_user_id", "username", "profile_picture_path") VALUES (1, 'testUser1', 'path/to/pic1');
INSERT INTO "users" ("main_user_id", "username", "profile_picture_path") VALUES (2, 'testUser2', 'path/to/pic2');

INSERT INTO "unfinished_movies" ("user_id", "video_path", "last_watched", "watched_seconds")
VALUES (1, 'path/to/video', '2025-06-10', 200);
