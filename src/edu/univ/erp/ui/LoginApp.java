package edu.univ.erp.ui;

import javax.swing.*;
import edu.univ.erp.data.DBConnection;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginApp extends JFrame {
    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginButton;
    JLabel messageLabel;

    public LoginApp() {
        setTitle("University ERP - Login");
        setSize(1000, 750);
        setLayout(new GridLayout(4, 2, 10, 10));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        loginButton = new JButton("Login");
        messageLabel = new JLabel("");
        add(loginButton);
        add(messageLabel);

        // Fixed action listener syntax
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Connection conn = DBConnection.getAuthConnection();
                    String sql = "SELECT role FROM users WHERE username=? AND password=?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, usernameField.getText());
                    ps.setString(2, new String(passwordField.getPassword()));
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        String role = rs.getString("role");
                        JOptionPane.showMessageDialog(null, "Login successful as " + role);
                        
                         if (role.equalsIgnoreCase("admin")) {
                            new AdminDashboard();  // open admin dashboard
                        } else if (role.equalsIgnoreCase("instructor")) {
                            new InstructorDashboard();
                        } else if (role.equalsIgnoreCase("student")) {
                            new StudentDashboard();
                        }
                                            // open respective dashboard here
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid username or password");
                    }

                    conn.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
                }
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginApp();
    }
}
