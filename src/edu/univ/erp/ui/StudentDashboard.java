package edu.univ.erp.ui;

import javax.swing.*;

import edu.univ.erp.access.StudentCommands;
import java.awt.*;
import edu.univ.erp.data.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.awt.event.*;
import java.sql.ResultSet;

public class StudentDashboard extends JFrame {

    public int getStudentIdByUsername(String username) {
        String query = "SELECT s.user_id FROM students s " +
                    "JOIN auth_db.users u ON s.user_id = u.id " +
                    "WHERE u.username = ?";

        try (Connection conn = DBConnection.getErpConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1; // not found
    }


    private StudentCommands sc = new StudentCommands();
    private int studentId;
    public StudentDashboard(String Username) {
        this.studentId = getStudentIdByUsername(Username);

        setTitle("Student Dashboard");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Title Label
        JLabel head = new JLabel("Welcome, Student!");
        head.setHorizontalAlignment(SwingConstants.CENTER);
        head.setFont(new Font("Arial", Font.BOLD, 22));
        add(head, BorderLayout.NORTH);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100));

        JButton viewCoursesBtn = new JButton("View Available Courses");
        JButton enrollBtn = new JButton("Enroll in Course");
        JButton myCoursesBtn = new JButton("My Enrollments");
        JButton gradesBtn = new JButton("View Grades");
        JButton profileBtn = new JButton("View Profile");
        JButton logoutBtn = new JButton("Logout");

        buttonPanel.add(viewCoursesBtn);
        buttonPanel.add(enrollBtn);
        buttonPanel.add(myCoursesBtn);
        buttonPanel.add(gradesBtn);
        buttonPanel.add(profileBtn);
        buttonPanel.add(logoutBtn);

        add(buttonPanel, BorderLayout.CENTER);

        // Button actions
        viewCoursesBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Showing available courses...");
            ResultSet rs = sc.browseCatalog();
            if (rs != null) {
                TablePopup.showResultSet(rs, "Available Courses");
            } else {
                JOptionPane.showMessageDialog(this, "No courses found.");
            }
        });

        enrollBtn.addActionListener(e -> {
            String secId = JOptionPane.showInputDialog(this, "Enter Section ID to enroll:");
            if (secId != null) {
                boolean ok = sc.registerForSection(studentId, Integer.parseInt(secId));
                JOptionPane.showMessageDialog(this, ok ? "Enrolled!" : "Failed to enroll.");
            }
        });

        myCoursesBtn.addActionListener(e -> {
            ResultSet rs = sc.viewTimetable(studentId);
            if (rs != null) {
                TablePopup.showResultSet(rs, "My Timetable");
            } else {
                JOptionPane.showMessageDialog(this, "No enrolled courses found.");
            }
        });

        gradesBtn.addActionListener(e -> {
        System.out.println("Student ID: " + studentId); // debug
        ResultSet rs = sc.viewGrades(studentId);

        if (rs != null) {
            TablePopup.showResultSet(rs, "My Grades");
        } else {
            JOptionPane.showMessageDialog(this, "No grades available.");
        }
    });


        // profileBtn.addActionListener(e -> {
        //     JOptionPane.showMessageDialog(this, "Showing student profile...");
        //     ResultSet rs = sc.viewProfile(studentId);
        //     if (rs != null) {
        //         TablePopup.showResultSet(rs, "Profile");
        //     } else {
        //         JOptionPane.showMessageDialog(this, "Profile not found.");
        //     }
        // });


        logoutBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Logging out...");
            dispose();
            new LoginApp();
        });

        setVisible(true);
    }

    // public static void main(String[] args) {
    //     new StudentDashboard();
    // }
}
