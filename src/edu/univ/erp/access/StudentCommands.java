package edu.univ.erp.access;

import edu.univ.erp.data.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudentCommands {

    // 1. Browse Course Catalog
    public ResultSet browseCatalog() {
        try {
            Connection conn = DBConnection.getErpConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT s.section_id, c.code, c.title, c.credits, s.capacity, " +
                "i.name AS instructor, s.day_time, s.room, s.semester, s.year " +
                "FROM sections s " +
                "JOIN courses c ON s.course_id = c.code " +
                "JOIN instructors i ON s.instructor_id = i.user_id"
            );
            return stmt.executeQuery(); 
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 2. Register for a section
    public boolean registerForSection(int studentId, int sectionId) {
        String checkDuplicate = "SELECT 1 FROM enrollments WHERE student_id = ? AND section_id = ?";
        String checkCapacity = "SELECT capacity, (SELECT COUNT(*) FROM enrollments WHERE section_id = ?) AS enrolled " +
                               "FROM sections WHERE section_id = ?";
        String insert = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'active')";

        try (Connection conn = DBConnection.getErpConnection()) {

            // Duplicate check
            try (PreparedStatement stmt = conn.prepareStatement(checkDuplicate)) {
                stmt.setInt(1, studentId);
                stmt.setInt(2, sectionId);
                var rs = stmt.executeQuery();
                if (rs.next()) return false;
            }

            // Capacity check
            try (PreparedStatement stmt = conn.prepareStatement(checkCapacity)) {
                stmt.setInt(1, sectionId);
                stmt.setInt(2, sectionId);
                var rs = stmt.executeQuery();
                if (rs.next() && rs.getInt("enrolled") >= rs.getInt("capacity")) return false;
            }

            // Register
            try (PreparedStatement stmt = conn.prepareStatement(insert)) {
                stmt.setInt(1, studentId);
                stmt.setInt(2, sectionId);
                return stmt.executeUpdate() > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. Drop a section
    public boolean dropSection(int studentId, int sectionId) {
        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM enrollments WHERE student_id = ? AND section_id = ?"
             )) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. View timetable
    public ResultSet viewTimetable(int studentId) {
        try {
            Connection conn = DBConnection.getErpConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT c.code, c.title, s.day_time, s.room, s.semester, s.year " +
                "FROM enrollments e " +
                "JOIN sections s ON e.section_id = s.section_id " +
                "JOIN courses c ON s.course_id = c.code " +
                "WHERE e.student_id = ?"
            );
            stmt.setInt(1, studentId);
            return stmt.executeQuery();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 5. View grades
    public ResultSet viewGrades(int studentId) {
        try {
            Connection conn = DBConnection.getErpConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT g.enrollment_id, g.component, g.score, g.final_grade " +
                "FROM grades g " +
                "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
                "WHERE e.student_id = ?"
            );
            stmt.setInt(1, studentId);
            return stmt.executeQuery();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
