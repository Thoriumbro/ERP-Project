package edu.univ.erp.ui;

import edu.univ.erp.access.AdminCommands;
import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Title Label
        JLabel head = new JLabel("Welcome, Admin!");
        head.setHorizontalAlignment(SwingConstants.CENTER);
        head.setFont(new Font("Arial", Font.BOLD, 22));
        add(head, BorderLayout.NORTH);

        AdminCommands admin = new AdminCommands();

        // Create buttons
        JButton addAdminBtn = new JButton("Add Admin");
        JButton addStudentBtn = new JButton("Add Student");
        JButton addInstructorBtn = new JButton("Add Instructor");
        JButton addCourseBtn = new JButton("Add Course");
        JButton editCourseBtn = new JButton("Edit Course");
        JButton addSectionBtn = new JButton("Add Section");
        JButton editSectionBtn = new JButton("Edit Section");

        // Panel for layout
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 1, 10, 10));
        panel.add(addAdminBtn);
        panel.add(addStudentBtn);
        panel.add(addInstructorBtn);
        panel.add(addCourseBtn);
        panel.add(editCourseBtn);
        panel.add(addSectionBtn);
        panel.add(editSectionBtn);

        add(panel, BorderLayout.CENTER);

        // Button actions
        addAdminBtn.addActionListener(e -> {
            String u = JOptionPane.showInputDialog("Enter username:");
            String p = JOptionPane.showInputDialog("Enter password:");
            boolean ok = admin.addAdmin(u, p);
            JOptionPane.showMessageDialog(this, ok ? "Admin added" : "Failed");
        });

        addStudentBtn.addActionListener(e -> {
            String n = JOptionPane.showInputDialog("Name:");
            String u = JOptionPane.showInputDialog("Username:");
            String p = JOptionPane.showInputDialog("Password:");
            String r = JOptionPane.showInputDialog("Roll No:");
            String prog = JOptionPane.showInputDialog("Program:");
            int year = Integer.parseInt(JOptionPane.showInputDialog("Year:"));
            boolean ok = admin.addStudent(u, p, n, r, prog, year);
            JOptionPane.showMessageDialog(this, ok ? "Student added" : "Failed");
        });
        
        addInstructorBtn.addActionListener(e -> {
            String n = JOptionPane.showInputDialog("Name:");
            String u = JOptionPane.showInputDialog("Username:");
            String p = JOptionPane.showInputDialog("Password:");
            String d = JOptionPane.showInputDialog("Department:");
            boolean ok = admin.addInstructor(u, p, n, d);
            JOptionPane.showMessageDialog(this, ok ? "Instructor added" : "Failed");
        });

        addCourseBtn.addActionListener(e -> {
            String code = JOptionPane.showInputDialog("Course Code:");
            String title = JOptionPane.showInputDialog("Title:");
            int credits = Integer.parseInt(JOptionPane.showInputDialog("Credits:"));
            boolean ok = admin.addCourse(code, title, credits);
            JOptionPane.showMessageDialog(this, ok ? "Course added" : "Failed");
        });

        editCourseBtn.addActionListener(e -> {
            String code = JOptionPane.showInputDialog("Course Code:");
            String field = JOptionPane.showInputDialog("Field to edit (title/credits):");
            String newVal = JOptionPane.showInputDialog("New Value:");
            Object val = field.equals("credits") ? Integer.parseInt(newVal) : newVal;
            boolean ok = admin.editCourse(field, val, code);
            JOptionPane.showMessageDialog(this, ok ? "Course updated" : "Failed");
        });

        addSectionBtn.addActionListener(e -> {
            String course = JOptionPane.showInputDialog("Course Code:");
            int ins = Integer.parseInt(JOptionPane.showInputDialog("Instructor ID:"));
            String dt = JOptionPane.showInputDialog("Day/Time:");
            String room = JOptionPane.showInputDialog("Room:");
            int cap = Integer.parseInt(JOptionPane.showInputDialog("Capacity:"));
            String sem = JOptionPane.showInputDialog("Semester:");
            int year = Integer.parseInt(JOptionPane.showInputDialog("Year:"));
            boolean ok = admin.addSection(course, ins, dt, room, cap, sem, year);
            JOptionPane.showMessageDialog(this, ok ? "Section added" : "Failed");
        });

        editSectionBtn.addActionListener(e -> {
            int id = Integer.parseInt(JOptionPane.showInputDialog("Section ID:"));
            String field = JOptionPane.showInputDialog("Field (day_time, room, capacity, semester, year, instructor_id):");
            String newVal = JOptionPane.showInputDialog("New Value:");
            Object val = (field.equals("capacity") || field.equals("year") || field.equals("instructor_id"))
                    ? Integer.parseInt(newVal) : newVal;
            boolean ok = admin.editSection(field, val, id);
            JOptionPane.showMessageDialog(this, ok ? "Section updated" : "Failed");
        });

        setVisible(true);
    }

    // public static void main(String[] args) {
    //     new AdminDashboard();
    // }
}
