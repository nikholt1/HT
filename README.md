# Refract

Read about the full development process here:
[Refract article](https://niklasholtlau.web.app/front.html)

* [Go to Recent updates](#Recent-updates)

## Table of contents
* [SWOT and Risk Analysis](https://github.com/nikholt1/HT/blob/master/analysis/HT%20SWOT%20analysis%20and%20Risk%20analysis.pdf)
* [Feasibility Study](https://github.com/nikholt1/HT/blob/master/analysis/HT%20Feasibility%20Study.pdf)
* [Go to Database Setup](#database-setup)
* [Go to Retrieve lost username and password guide](#Retreive-username-and-password-guide)

  
<!--What the project does
    Why the project is useful
    How users can get started with the project
    Where users can get help with your project
    Who maintains and contributes to the project--> 

## About
This project is a locally run web-application, allowing users to view and stream videos from a locally stored
host computer. The System is a desktop application that through Springboot framework serves a web-application on the users Wi-Fi, allowing
anyone on the same Wi-Fi to access the specified folders with the content and stream videos from said web-application.
The system installs a default folder structure and executable application, that together with a SQLite database lets the user register profiles and stream the content from them.
The system also allows the user to add new folders or modify the existing default folders for categories of content.


## Why
Given the rising annoyance of streaming services' rising prices together with more ads implementations and
since most of my projects are not made for public display, i decided to make this locally run streaming service.

## How to use
#### Installation
I have designed an installer script that can be downloaded, which through automation creates the necessary folder-structure
and installs the executable and assets in the default folders, together with the default SQLite database.

#### Modification of the system
From here the user has the option to specify a certain directory, if they please, or they can populate the 
default folders with the videos they want. To add a image to a specific video, the user can place the video with the name (video.mp4) and in the same folder
place a image with the exact same name as the video (video.jpg). From there the system will automatically update the backend to serve the video
and place the cover of the video with the dedicated image to the frontend.

#### Lost username and password
The default Username and Password for the login, is username: Admin and password: admin. After initial login the user has the option and is encouraged to 
change the username and password to what they desire. This is locally stored on their host computer, which means that the system does not send any data to anywhere, which means that if the username and password is lost,
the user would have to follow the "retrieve username and password" guide, to fetch the lost username and password directly from the SQLite database.

## Maintenance and contributions
#### Github versions and Automatic update
The prod system automatically searches for the latest version on Github versions, and automatically updates itself after comparing its own version to the newest version.
The version control is maintained by me and contributions that are accepted will be implemented in newer versions of the system once i roll out a new version of the system to Github version. 

<!-- Database Setup section -->

# Database Setup

This database is designed to manage a single main account and multiple user profiles, each with their own unfinished movies.

## Tables

### `main_user`
- **Purpose:** Stores the application login.
- **Columns:**
  - `main_user_id` – Primary Key
  - `username` – Login name
  - `password_hash` – Hashed password
- **Notes:** Only one `main_user` exists in the system.

### `users`
- **Purpose:** Stores individual profiles under the main account.
- **Columns:**
  - `user_id` – Primary Key
  - `username` – Profile name
- **Notes:** Profiles are implicitly linked to the single `main_user`.

### `unfinished_movies`
- **Purpose:** Tracks movie progress per profile.
- **Columns:**
  - `user_id` – Foreign Key → `users.user_id`
  - `video_path` – Path to the movie
  - `last_watched` – Timestamp of last watch
  - `watched_seconds` – Movie progress in seconds
- **Primary Key:** `(user_id, video_path)` – ensures one record per profile per movie

---

## How the System Uses the Database

1. **Log in** with the single `main_user` (username + password).  
2. **Select a profile** from the `users` table.  
3. **List unfinished movies** by querying `unfinished_movies` filtered by `user_id`.  
4. **Resume a movie** by querying `unfinished_movies` with `(user_id, video_path)` to retrieve `watched_seconds`.

---
<img width="342" height="572" alt="image" src="https://github.com/user-attachments/assets/7abfc66a-fee7-4b84-8419-050775face32" />

## DBML Diagram

```dbml
Table main_user {
  main_user_id integer [pk]
  username varchar
  password_hash varchar
}

Table users {
  user_id integer [pk]
  main_user_id integer 
  username varchar
}

Table unfinished_movies {
  user_id integer 
  video_path varchar
  last_watched timestamp
  watched_seconds integer

  indexes {
    (user_id, video_path) [pk] 
  }
}

Ref: users.main_user_id > main_user.main_user_id
Ref: unfinished_movies.user_id > users.user_id
```

## Final Words

The system follows 3rd Normal Form (3NF) because each table stores only relevant data:  
- All columns are atomic.  
- Non-key data depends on the entire primary key.  
- There are no transitive dependencies between non-key columns.
- This ensures minimal redundancy, clear relationships, and consistent movie progress tracking per profile.


## Database: notes
- Implemented user_profile_picture_path varchar(255) into database for profile picture handling per user.
08/10/2025

<!-- Database Setup section END -->


## Retreive username and password guide

## Recent updates