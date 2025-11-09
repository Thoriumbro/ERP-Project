package edu.univ.erp.access;

import edu.univ.erp.data.DBConnection;
import java.sql.*;
import java.util.*;

public class InstructorCommands {

    public List<Integer> getMySections(int instructorId, String semester, int year) {
        List<Integer> sections = new ArrayList<>();
        String sql = "SELECT section_id FROM sections WHERE instructor_id = ? AND semester = ? AND year = ?";

        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, instructorId);
            stmt.setString(2, semester);
            stmt.setInt(3, year);

            var rs = stmt.executeQuery();
            while (rs.next()) sections.add(rs.getInt(1));

        } catch (Exception e) { e.printStackTrace(); }

        return sections;
    }

    public boolean saveScore(int sectionId, int studentId, String assessment, double score, double weight) {
        String sql = "REPLACE INTO scores (section_id, student_id, assessment, score, weight) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);
            stmt.setInt(2, studentId);
            stmt.setString(3, assessment);
            stmt.setDouble(4, score);
            stmt.setDouble(5, weight);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<Integer, Double> computeFinalGrades(int sectionId) {
        Map<Integer, Double> finalGrades = new HashMap<>();

        String sql = "SELECT student_id, SUM(score * (weight / 100)) AS finalScore " +
                     "FROM scores WHERE section_id = ? GROUP BY student_id";

        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);
            var rs = stmt.executeQuery();

            while (rs.next())
                finalGrades.put(rs.getInt("student_id"), rs.getDouble("finalScore"));

        } catch (Exception e) { e.printStackTrace(); }

        return finalGrades;
    }

    public Map<String, Double> getClassStats(int sectionId) {
        Map<String, Double> stats = new HashMap<>();

        String sql = "SELECT AVG(finalScore), MIN(finalScore), MAX(finalScore) " +
                     "FROM (SELECT SUM(score * (weight / 100)) AS finalScore " +
                     "      FROM scores WHERE section_id = ? GROUP BY student_id) AS t";

        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);
            var rs = stmt.executeQuery();

            if (rs.next()) {
                stats.put("average", rs.getDouble(1));
                stats.put("min", rs.getDouble(2));
                stats.put("max", rs.getDouble(3));
            }

        } catch (Exception e) { e.printStackTrace(); }

        return stats;
    }
}
