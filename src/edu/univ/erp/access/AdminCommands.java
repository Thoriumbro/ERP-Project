package edu.univ.erp.access;

import edu.univ.erp.data.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AdminCommands {

    public boolean addUser(String username, String password, String role) {
        try (Connection conn = DBConnection.getAuthConnection();
            PreparedStatement stmt = conn.prepareStatement(Query.INSERT_USER)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // public static void main(String[] args) {
    //     UserAccess ua = new UserAccess();
    //     boolean ok = ua.addUser("advik", "yo", "student");

    //     if (ok) {
    //         System.out.println("User added");
    //     } else {
    //         System.out.println("Failed to add user");
    //     }
    // }
}


