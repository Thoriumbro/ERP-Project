package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles database connections for the ERP project.
 * Update USER and PASSWORD with your MySQL credentials.
 */

public class DBConnection {

    private static final String URL_AUTH = "jdbc:mysql://localhost:3306/auth_db";
    private static final String URL_ERP = "jdbc:mysql://localhost:3306/erp_db";
    private static final String USER = "root";            
    private static final String PASSWORD = "qwertyuiop";
       
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
