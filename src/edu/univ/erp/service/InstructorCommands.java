package edu.univ.erp.service;

import edu.univ.erp.data.DBConnection;

import java.io.FileWriter;
import java.sql.*;
import java.util.*;

public class InstructorCommands {
    
    public ResultSet getMySections(int instructorId, String semester, int year) {
        String sql = "SELECT * FROM sections WHERE instructor_id = ? AND semester = ? AND year = ?";

        try {
            Connection conn = DBConnection.getErpConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, instructorId);
            stmt.setString(2, semester);
            stmt.setInt(3, year);

            return stmt.executeQuery(); 

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean saveScore(int sectionId, int studentId, String assessment, double score, double weight) {

        String checkSql = "SELECT 1 FROM enrollments WHERE section_id = ? AND student_id = ?";
        String saveSql = "REPLACE INTO scores (section_id, student_id, assessment, score, weight) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getErpConnection()) {

            // Check enrollment
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, sectionId);
                checkStmt.setInt(2, studentId);

                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next()) {
                    System.out.println("Student not enrolled — cannot save score.");
                    return false; 
                }
            }

            // Save score
            try (PreparedStatement stmt = conn.prepareStatement(saveSql)) {
                stmt.setInt(1, sectionId);
                stmt.setInt(2, studentId);
                stmt.setString(3, assessment);
                stmt.setDouble(4, score);
                stmt.setDouble(5, weight);

                return stmt.executeUpdate() > 0;
            }

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

    public boolean editSection(String field, Object newValue, int sectionId) {
        Set<String> allowed = Set.of("day_time", "capacity");
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

    public boolean exportScoresToCSV(int instructorId, String filePath) {
        String sql = """
            SELECT s.section_id, sc.student_id, sc.assessment, sc.score, sc.weight
            FROM sections s
            JOIN scores sc ON s.section_id = sc.section_id
            WHERE s.instructor_id = ?
            ORDER BY s.section_id, sc.student_id
        """;

        try (Connection conn = DBConnection.getErpConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();

            java.io.FileWriter writer = new java.io.FileWriter(filePath);

            writer.write("Section ID, Student ID, Assessment, Score, Weight\n");

            while (rs.next()) {
                writer.write(
                    rs.getInt("section_id") + "," +
                    rs.getInt("student_id") + "," +
                    rs.getString("assessment") + "," +
                    rs.getDouble("score") + "," +
                    rs.getDouble("weight") + "\n"
                );
            }

            writer.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
