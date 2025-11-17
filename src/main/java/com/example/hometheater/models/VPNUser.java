package com.example.hometheater.models;

import org.apache.catalina.User;

import java.time.LocalDateTime;

public class VPNUser {


    private User user;
    private int userId;
    private int VPNUserId;
    private String ovpnFileName;
    private boolean active = true;
    private boolean downloaded = false;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime revokedAt;


    public VPNUser() {

    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getVPNUserId() {
        return VPNUserId;
    }

    public void setVPNUserId(int VPNUserId) {
        this.VPNUserId = VPNUserId;
    }

    public String getOvpnFileName() {
        return ovpnFileName;
    }

    public void setOvpnFileName(String ovpnFileName) {
        this.ovpnFileName = ovpnFileName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(LocalDateTime revokedAt) {
        this.revokedAt = revokedAt;
    }
}
