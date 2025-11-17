package com.example.hometheater.repository;


import com.example.hometheater.models.VPNUser;
import com.example.hometheater.repository.DAO.VPNUserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OpenVPNRepository {

    private final VPNUserDAO vpnUserDAO;

    @Autowired
    public OpenVPNRepository(VPNUserDAO vpnUserDAO) {
        this.vpnUserDAO = vpnUserDAO;
    }

    // ===== CREATE =====
    public VPNUser createVPNUser(int userId, String ovpnFileName) {
        int rows = vpnUserDAO.createVPNUser(userId, ovpnFileName);
        if (rows > 0) {
            // Retrieve the last inserted VPN user
            // Since SQLite AUTOINCREMENT, get the last row
            return vpnUserDAO.getVPNUsersByUserId(userId)
                    .stream()
                    .max((a, b) -> Integer.compare(a.getVPNUserId(), b.getVPNUserId()))
                    .orElse(null);
        }
        return null;
    }

    // ===== READ =====
    public VPNUser getVPNUserById(int vpnUserId) {
        return vpnUserDAO.getVPNUserById(vpnUserId);
    }

    public List<VPNUser> getVPNUsersByUserId(int userId) {
        return vpnUserDAO.getVPNUsersByUserId(userId);
    }

    // ===== UPDATE =====
    public boolean setVPNUserActive(int vpnUserId, boolean active) {
        return vpnUserDAO.updateVPNUserStatus(vpnUserId, active) > 0;
    }

    // ===== DELETE =====
    public boolean deleteVPNUser(int vpnUserId) {
        return vpnUserDAO.deleteVPNUser(vpnUserId) > 0;
    }



    ///  create read and update from the open vpn config files
    ///  likewise install and configure open VPN
    ///  generate user keys and give them to the VPNUsers, so the profiles can fetch depending on who they are connected to in the vpn users.


}

