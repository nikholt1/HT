[Go to Database Setup](#database-setup)




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
- Implemented
  user_profile_picture_path varchar(255)

into database for profile picture handling per user.
08/10/2025

<!-- Database Setup section END -->
