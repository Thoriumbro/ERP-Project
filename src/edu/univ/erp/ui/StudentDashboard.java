package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StudentDashboard extends JFrame {

    public StudentDashboard() {
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
        viewCoursesBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Showing available courses...")
        );

        enrollBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Enroll in a selected course...")
        );

        myCoursesBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Showing your enrolled courses...")
        );

        gradesBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Showing your grades...")
        );

        profileBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Showing student profile...")
        );

        logoutBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Logging out...");
            dispose();
            new LoginApp(); // return to login screen
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new StudentDashboard();
    }
}
