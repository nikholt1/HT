package com.example.hometheater.models;



public class MainUser {
    private int main_user_id;
    private String username;
    private String password; // hashed

    public MainUser() {}

    public MainUser(String username, String password, int main_user_id) {
        this.username = username;
        this.password = password;
        this.main_user_id = main_user_id;
    }

    public String getUsername() {
        System.out.println("[SYSTEM] Retrieving MainUser username " + username);
        return username;
    }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() {
        System.out.println("[SYSTEM] Retrieving MainUser password " + password);

        return password; }
    public int getMain_user_id() {
        return main_user_id;
    }
    public void setPassword(String password) { this.password = password; }
}
