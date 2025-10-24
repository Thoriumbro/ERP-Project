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
    private static final String USER = "root";             // your MySQL username
    private static final String PASSWORD = "qwertyuiop"; // your MySQL password

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // load the MySQL JDBC driver
        } catch (ClassNotFoundException e) {
            System.out.println("❌ JDBC Driver not found: " + e.getMessage());
        }
    }

    /** 
     * Connects to auth_db (for login & roles)
     */
    public static Connection getAuthConnection() {
        try {
            return DriverManager.getConnection(URL_AUTH, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("❌ Failed to connect to auth_db: " + e.getMessage());
            return null;
        }
    }

    /**
     * Connects to erp_db (for student, instructor, courses)
     */
    public static Connection getErpConnection() {
        try {
            return DriverManager.getConnection(URL_ERP, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("❌ Failed to connect to erp_db: " + e.getMessage());
            return null;
        }
    }
}
