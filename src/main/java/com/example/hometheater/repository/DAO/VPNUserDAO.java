package com.example.hometheater.repository.DAO;


import com.example.hometheater.models.VPNUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VPNUserDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ===== CREATE =====
    public int createVPNUser(int userId, String ovpnFileName) {
        String sql = "INSERT INTO vpn_users (user_id, ovpn_file_name) VALUES (?, ?)";
        return jdbcTemplate.update(sql, userId, ovpnFileName);
    }

    // ===== READ by VPN user id =====
    public VPNUser getVPNUserById(int vpnUserId) {
        String sql = "SELECT * FROM vpn_users WHERE vpn_user_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{vpnUserId}, (rs, rowNum) -> {
            VPNUser vpnUser = new VPNUser();
            vpnUser.setVPNUserId(rs.getInt("vpn_user_id"));
            vpnUser.setUserId(rs.getInt("user_id"));
            vpnUser.setOvpnFileName(rs.getString("ovpn_file_name"));
            vpnUser.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            vpnUser.setActive(rs.getInt("active") == 1);
            vpnUser.setDownloaded(rs.getInt("downloaded") == 1);
            vpnUser.setRevokedAt(rs.getTimestamp("revoked_at") != null
                    ? rs.getTimestamp("revoked_at").toLocalDateTime()
                    : null);
            return vpnUser;
        });
    }

    // ===== READ by user id =====
    public List<VPNUser> getVPNUsersByUserId(int userId) {
        String sql = "SELECT * FROM vpn_users WHERE user_id = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, (rs, rowNum) -> {
            VPNUser vpnUser = new VPNUser();
            vpnUser.setVPNUserId(rs.getInt("vpn_user_id"));
            vpnUser.setUserId(rs.getInt("user_id"));
            vpnUser.setOvpnFileName(rs.getString("ovpn_file_name"));
            vpnUser.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            vpnUser.setActive(rs.getInt("active") == 1);
            vpnUser.setDownloaded(rs.getInt("downloaded") == 1);
            vpnUser.setRevokedAt(rs.getTimestamp("revoked_at") != null
                    ? rs.getTimestamp("revoked_at").toLocalDateTime()
                    : null);
            return vpnUser;
        });
    }

    // ===== UPDATE (example: activate/deactivate) =====
    public int updateVPNUserStatus(int vpnUserId, boolean active) {
        String sql = "UPDATE vpn_users SET active = ? WHERE vpn_user_id = ?";
        return jdbcTemplate.update(sql, active ? 1 : 0, vpnUserId);
    }

    // ===== DELETE =====
    public int deleteVPNUser(int vpnUserId) {
        String sql = "DELETE FROM vpn_users WHERE vpn_user_id = ?";
        return jdbcTemplate.update(sql, vpnUserId);
    }
}

