package edu.univ.erp.auth;

import edu.univ.erp.data.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MaintenanceMode {

    private static final String KEY = "maintenance_mode";

    public void enable() {
        String sql = "UPDATE `SETTINGS` SET `value` = ? WHERE `key` = ?";

        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "true");
            stmt.setString(2, KEY);
            stmt.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error enabling maintenance mode: " + e.getMessage());
        }
    }

    public void disable() {
        String sql = "UPDATE `SETTINGS` SET `value` = ? WHERE `key` = ?";

        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "false");
            stmt.setString(2, KEY);
            stmt.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error disabling maintenance mode: " + e.getMessage());
        }
    }

    public boolean isEnabled() {
        String sql = "SELECT `value` FROM `SETTINGS` WHERE `key` = ?";

        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, KEY);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("value").equalsIgnoreCase("true");
            }

        } catch (Exception e) {
            System.out.println("Error checking maintenance mode: " + e.getMessage());
        }

        return false;
    }
}
