package edu.univ.erp.access;

import edu.univ.erp.data.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

/*
Tables in auth_db:
    1. users
Tables in erp_db:
    1. courses          
    2. enrollments      
    3. grades           
    4. instructors      
    5. sections         
    6. settings         
    7. students 
*/

public class AdminCommands {

    public boolean addUser(String username, String password, String role) {
        try (Connection conn = DBConnection.getAuthConnection();
            PreparedStatement stmt = conn.prepareStatement(Query.insertUser("users"))) {

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

    public boolean addCourse(String code, String title, int credits) {
        try (Connection conn = DBConnection.getErpConnection();
            PreparedStatement stmt = conn.prepareStatement(Query.insertCourse("courses"))) {

            stmt.setString(1, code);
            stmt.setString(2, title);
            stmt.setInt(3, credits);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean editCourse(String field, Object newValue, String code) {

        String sql = "UPDATE courses SET " + field + " = ? WHERE code = ?";

        try (Connection conn = DBConnection.getErpConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, newValue);
            stmt.setString(2, code);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean addSection(int course_id, int instructor_id, int credits, String day_time, String room, int capacity, String semester, int year) {
        try (Connection conn = DBConnection.getErpConnection();
            PreparedStatement stmt = conn.prepareStatement(Query.insertSection("sections"))) {

            stmt.setInt(1, course_id);
            stmt.setInt(2, instructor_id);
            stmt.setInt(3, credits);
            stmt.setString(4, day_time);
            stmt.setString(5, room);
            stmt.setInt(6, capacity);
            stmt.setString(6, semester);
            stmt.setInt(7, year);


            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean editSection(String field, Object newValue, int sectionId) {

        String sql = "UPDATE sections SET " + field + " = ? WHERE section_id = ?";

        try (Connection conn = DBConnection.getErpConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, newValue);
            stmt.setInt(2, sectionId);

            return stmt.executeUpdate() > 0;

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


