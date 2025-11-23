package edu.univ.erp.access;

import edu.univ.erp.data.DBConnection;

import java.io.FileWriter;
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

        String checkDeadlineQuery = """
            SELECT c.deadline 
            FROM courses c
            JOIN sections s ON s.course_code = c.code
            WHERE s.id = ?
        """;

        String deleteQuery = "DELETE FROM enrollments WHERE student_id = ? AND section_id = ?";

        try (Connection conn = DBConnection.getErpConnection();
            PreparedStatement checkStmt = conn.prepareStatement(checkDeadlineQuery);
            PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {

            // Step 1: Check deadline
            checkStmt.setInt(1, sectionId);
            var rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("No deadline found. Drop denied.");
                return false;
            }

            java.sql.Date deadline = rs.getDate("deadline");
            java.sql.Date today = java.sql.Date.valueOf(java.time.LocalDate.now());

            // Step 2: If deadline passed, disallow drop
            if (today.after(deadline)) {
                System.out.println("Cannot drop. Deadline passed.");
                return false;
            }

            // Step 3: Allow deletion if still before or on deadline
            deleteStmt.setInt(1, studentId);
            deleteStmt.setInt(2, sectionId);

            return deleteStmt.executeUpdate() > 0;

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

    public boolean exportGradesToCSV(int studentId, String filePath) {
        String[] headers = {"Section ID", "Assessment", "Score", "Weight", "Final Grade" };

        try (Connection conn = DBConnection.getErpConnection();
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
            FileWriter writer = new FileWriter(filePath)
        ) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, studentId);

            ResultSet rs = stmt.executeQuery();

            // Write CSV header
            writer.append(String.join(",", headers)).append("\n");

            // Write rows
            while (rs.next()) {
                writer.append(rs.getString("section_id")).append(",");
                writer.append(rs.getString("assessment")).append(",");
                writer.append(rs.getString("score") == null ? "" : rs.getString("score")).append(",");
                writer.append(rs.getString("weight") == null ? "" : rs.getString("weight")).append(",");
                writer.append(rs.getString("final_grade") == null ? "" : rs.getString("final_grade")).append("\n");
            }

            writer.flush();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
