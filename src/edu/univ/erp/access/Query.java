package edu.univ.erp.access;

public class Query {

    public static String insertUser(String table) {
        return "INSERT INTO " + table + " (username, password, role) VALUES (?, ?, ?)";
    }

    public static String getUserByUsername(String table) {
        return "SELECT * FROM " + table + " WHERE username = ?";
    }

    public static String deleteUser(String table) {
        return "DELETE FROM " + table + " WHERE username = ?";
    }

    public static String insertCourse(String table) {
        return "INSERT INTO " + table + " (code, title, credits) VALUES (?, ?, ?)";
    }

    public static String insertSection(String table) {
        return "INSERT INTO " + table + " (course_id, instructor_id, day_time, room, capacity, semester, year) VALUES (?, ?, ?, ?, ?, ?, ?)";
    }
}
