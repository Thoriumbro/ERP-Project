package edu.univ.erp.access;

import edu.univ.erp.data.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Set;

public class AdminCommands {

    public boolean addAdmin(String username, String password) {
        try (Connection conn = DBConnection.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO users (username, password, role) VALUES (?, ?, 'admin')")) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addStudent(String username, String password, String rollNo, String program, int year) {
        try (Connection authConn = DBConnection.getAuthConnection();
             PreparedStatement userStmt = authConn.prepareStatement(
                     "INSERT INTO users (username, password, role) VALUES (?, ?, 'student')",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {

            userStmt.setString(1, username);
            userStmt.setString(2, password);

            if (userStmt.executeUpdate() == 0) return false;

            var rs = userStmt.getGeneratedKeys();
            if (!rs.next()) return false;
            int userId = rs.getInt(1);

            try (Connection erpConn = DBConnection.getErpConnection();
                 PreparedStatement stmt = erpConn.prepareStatement(
                         "INSERT INTO students (user_id, roll_no, program, year) VALUES (?, ?, ?, ?)")) {

                stmt.setInt(1, userId);
                stmt.setString(2, rollNo);
                stmt.setString(3, program);
                stmt.setInt(4, year);

                return stmt.executeUpdate() > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addInstructor(String username, String password, String name, String department) {
        try (Connection authConn = DBConnection.getAuthConnection();
             PreparedStatement userStmt = authConn.prepareStatement(
                     "INSERT INTO users (username, password, role) VALUES (?, ?, 'instructor')",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {

            userStmt.setString(1, username);
            userStmt.setString(2, password);

            if (userStmt.executeUpdate() == 0) return false;

            var rs = userStmt.getGeneratedKeys();
            if (!rs.next()) return false;
            int userId = rs.getInt(1);

            try (Connection erpConn = DBConnection.getErpConnection();
                 PreparedStatement stmt = erpConn.prepareStatement(
                         "INSERT INTO instructors (user_id, name, department) VALUES (?, ?, ?)")) {

                stmt.setInt(1, userId);
                stmt.setString(2, name);
                stmt.setString(3, department);

                return stmt.executeUpdate() > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addCourse(String code, String title, int credits) {
        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO courses (code, title, credits) VALUES (?, ?, ?)")) {

            stmt.setString(1, code);
            stmt.setString(2, title);
            stmt.setInt(3, credits);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean editCourse(String field, Object newValue, String code) {
        Set<String> allowed = Set.of("title", "credits");
        if (!allowed.contains(field)) return false;

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

    public boolean addSection(String courseCode, int instructorId, String dayTime, String room,
                              int capacity, String semester, int year) {

        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO sections (course_id, instructor_id, day_time, room, capacity, semester, year) VALUES (?, ?, ?, ?, ?, ?, ?)")) {

            stmt.setString(1, courseCode);
            stmt.setInt(2, instructorId);
            stmt.setString(3, dayTime);
            stmt.setString(4, room);
            stmt.setInt(5, capacity);
            stmt.setString(6, semester);
            stmt.setInt(7, year);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean editSection(String field, Object newValue, int sectionId) {
        Set<String> allowed = Set.of("day_time", "room", "capacity", "semester", "year", "instructor_id");
        if (!allowed.contains(field)) return false;

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
}
