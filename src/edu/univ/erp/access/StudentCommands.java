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
    String checkCapacity = "SELECT capacity FROM sections WHERE section_id = ?";
    String insert = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'active')";
    String reduceCapacity = "UPDATE sections SET capacity = capacity - 1 WHERE section_id = ?";

    try (Connection conn = DBConnection.getErpConnection()) {

        // Duplicate check
        try (PreparedStatement stmt = conn.prepareStatement(checkDuplicate)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return false;
        }

        // Capacity check
        try (PreparedStatement stmt = conn.prepareStatement(checkCapacity)) {
            stmt.setInt(1, sectionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt("capacity") <= 0) return false;
        }

        // Register the student
        try (PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            stmt.executeUpdate();
        }

        // Reduce capacity by 1
        try (PreparedStatement stmt = conn.prepareStatement(reduceCapacity)) {
            stmt.setInt(1, sectionId);
            stmt.executeUpdate();
        }

        return true;

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
                "(SELECT section_id, assessment, score, weight, NULL AS final_grade " +
                " FROM scores " +
                " WHERE student_id = ?) " +
                "UNION ALL " +
                "(SELECT section_id, 'Final Grade' AS assessment, NULL AS score, NULL AS weight, " +
                "        SUM(score * (weight / 100)) AS final_grade " +
                " FROM scores " +
                " WHERE student_id = ? " +
                " GROUP BY section_id) " +
                "ORDER BY section_id, final_grade IS NULL DESC, assessment"
            );

            stmt.setInt(1, studentId);
            stmt.setInt(2, studentId);

            return stmt.executeQuery();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }





}
