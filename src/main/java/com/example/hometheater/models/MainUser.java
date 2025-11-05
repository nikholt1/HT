package com.example.hometheater.models;



public class MainUser {
    private int main_user_id;
    private String username;
    private String password_hash; // hashed

    public MainUser() {}

    public MainUser(String username, String password_hash, int main_user_id) {
        this.username = username;
        this.password_hash = password_hash;
        this.main_user_id = main_user_id;
    }

    public String getUsername() {
        System.out.println("[SYSTEM] Retrieving MainUser username " + username);
        return username;
    }
    public void setUsername(String username) { this.username = username; }

    public String getPassword_hash() {
        System.out.println("[SYSTEM] Retrieving MainUser password " + password_hash);

        return password_hash; }
    public int getMain_user_id() {
        return main_user_id;
    }
    public void setPassword_hash(String password_hash) { this.password_hash = password_hash; }
}
