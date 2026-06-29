package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import edu.univ.erp.util.Config;

/**
 * Handles database connections for the ERP project.
 * Update USER and PASSWORD with your MySQL credentials.
 */

public class DBConnection {

    private static final String URL_AUTH = Config.get("auth.url");
    private static final String URL_ERP = Config.get("db.url");
    private static final String USER = Config.get("db.user");       
    private static final String PASSWORD = Config.get("db.password");
       
    public static Connection getAuthConnection() {
        try {
            return DriverManager.getConnection(URL_AUTH, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Failed to connect to auth_db: " + e.getMessage());
            return null;
        }
    }


    public static Connection getErpConnection() {
        try {
            return DriverManager.getConnection(URL_ERP, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Failed to connect to erp_db: " + e.getMessage());
            return null;
        }
    }
}
