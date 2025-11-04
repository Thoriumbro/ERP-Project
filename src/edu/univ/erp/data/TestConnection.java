package edu.univ.erp.data;

import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        try (Connection authCon = DBConnection.getAuthConnection();
             Connection erpCon = DBConnection.getErpConnection()) {
            if (authCon != null) {
                System.out.println("✅ Connected to auth_db successfully!");
            } else {
                System.out.println("❌ Failed to connect to auth_db.");
            }

            if (erpCon != null) {
                System.out.println("✅ Connected to erp_db successfully!");
            } else {
                System.out.println("❌ Failed to connect to erp_db.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
