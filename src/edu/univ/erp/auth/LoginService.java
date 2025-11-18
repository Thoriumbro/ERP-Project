package edu.univ.erp.auth;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginService {

    public void updateLastLogin(String username, Connection conn) throws Exception {
        PreparedStatement update = conn.prepareStatement(
                "UPDATE users SET last_login = NOW() WHERE username = ?");
        update.setString(1, username);
        update.executeUpdate();
        update.close();
    }

    public void showLastLoginMessage(String role, String lastLogin) {
        if (lastLogin != null) {
            JOptionPane.showMessageDialog(null,
                    "Login successful as " + role +
                            "\nLast login: " + lastLogin);
        } else {
            JOptionPane.showMessageDialog(null,
                    "Login successful as " + role);
        }
    }
}
