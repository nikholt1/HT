package com.example.hometheater.models;

public class ProfileUser {

    private int userId;                 // optional, if you need it
    private String username;
    private String profilePicturePath;

    // Constructors
    public ProfileUser() {}

    public ProfileUser(int userId, String username, String profilePicturePath) {
        this.userId = userId;
        this.username = username;
        this.profilePicturePath = profilePicturePath;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }
}

