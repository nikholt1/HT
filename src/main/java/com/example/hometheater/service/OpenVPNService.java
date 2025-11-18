package com.example.hometheater.service;


import com.example.hometheater.models.VPNUser;
import com.example.hometheater.repository.DAO.DataAccessObject;
import com.example.hometheater.repository.OpenVPNRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenVPNService {


    private OpenVPNRepository vpnRepository;
    public OpenVPNService(OpenVPNRepository vpnRepository) {
        this.vpnRepository = vpnRepository;
    }

    public VPNUser createVPNUser(int userID, String ovpnFileName) {
        return vpnRepository.createVPNUser(userID, ovpnFileName);
    }

    public VPNUser getVPNUser(int userID) {
        return vpnRepository.getVPNUserById(userID);
    }

    public List<VPNUser> getAllVPNUsers() {
        return vpnRepository.getAllVPNUsers();
    }

    public boolean setVPNUserActive(int vpnUserId, boolean active) {
        return vpnRepository.setVPNUserActive(vpnUserId, active);
    }
    public boolean deleteVPNUser(int vpnUserId) {
        return vpnRepository.deleteVPNUser(vpnUserId);
    }




}
