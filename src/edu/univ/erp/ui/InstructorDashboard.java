package edu.univ.erp.ui;

import javax.swing.*;

public class InstructorDashboard extends JFrame {
    public InstructorDashboard() {
        setTitle("Instructor Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Welcome, Instructor!");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label);

        setVisible(true);
    }
}
