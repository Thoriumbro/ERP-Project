package edu.univ.erp.ui;

import edu.univ.erp.access.InstructorCommands;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class InstructorDashboard extends JFrame {

    public InstructorDashboard() {
        setTitle("Instructor Dashboard");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Title Label
        JLabel head = new JLabel("Welcome, Professor!");
        head.setHorizontalAlignment(SwingConstants.CENTER);
        head.setFont(new Font("Arial", Font.BOLD, 22));
        add(head, BorderLayout.NORTH);

        InstructorCommands instructor = new InstructorCommands();

        // Buttons
        JButton viewSectionsBtn = new JButton("View My Sections");
        JButton saveScoreBtn = new JButton("Save Student Score");
        JButton computeGradesBtn = new JButton("Compute Final Grades");
        JButton viewStatsBtn = new JButton("View Class Stats");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));
        panel.add(viewSectionsBtn);
        panel.add(saveScoreBtn);
        panel.add(computeGradesBtn);
        panel.add(viewStatsBtn);

        add(panel, BorderLayout.CENTER);

        // View My Sections
        viewSectionsBtn.addActionListener(e -> {
            int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Instructor ID:"));
            String sem = JOptionPane.showInputDialog("Enter Semester:");
            int year = Integer.parseInt(JOptionPane.showInputDialog("Enter Year:"));
            java.util.List<Integer> list = instructor.getMySections(id, sem, year);
            JOptionPane.showMessageDialog(this,
                    list.isEmpty() ? "No sections found." : "Your Sections: " + list.toString());
        });

        // Save Student Score
        saveScoreBtn.addActionListener(e -> {
            int sectionId = Integer.parseInt(JOptionPane.showInputDialog("Section ID:"));
            int studentId = Integer.parseInt(JOptionPane.showInputDialog("Student ID:"));
            String assessment = JOptionPane.showInputDialog("Assessment (e.g., Midterm):");
            double score = Double.parseDouble(JOptionPane.showInputDialog("Score:"));
            double weight = Double.parseDouble(JOptionPane.showInputDialog("Weight (%):"));
            boolean ok = instructor.saveScore(sectionId, studentId, assessment, score, weight);
            JOptionPane.showMessageDialog(this, ok ? "Score saved." : "Failed to save.");
        });

        // Compute Final Grades
        computeGradesBtn.addActionListener(e -> {
            int sectionId = Integer.parseInt(JOptionPane.showInputDialog("Section ID:"));
            Map<Integer, Double> grades = instructor.computeFinalGrades(sectionId);
            if (grades.isEmpty())
                JOptionPane.showMessageDialog(this, "No scores found.");
            else {
                StringBuilder sb = new StringBuilder("Final Grades:\n");
                for (var entry : grades.entrySet())
                    sb.append("Student ").append(entry.getKey())
                      .append(": ").append(String.format("%.2f", entry.getValue())).append("\n");
                JOptionPane.showMessageDialog(this, sb.toString());
            }
        });

        // View Class Stats
        viewStatsBtn.addActionListener(e -> {
            int sectionId = Integer.parseInt(JOptionPane.showInputDialog("Section ID:"));
            Map<String, Double> stats = instructor.getClassStats(sectionId);
            if (stats.isEmpty())
                JOptionPane.showMessageDialog(this, "No data found.");
            else {
                String msg = "Average: " + String.format("%.2f", stats.get("average")) +
                             "\nMin: " + String.format("%.2f", stats.get("min")) +
                             "\nMax: " + String.format("%.2f", stats.get("max"));
                JOptionPane.showMessageDialog(this, msg);
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new InstructorDashboard();
    }
}
