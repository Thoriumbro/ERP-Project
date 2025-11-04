package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkLogin();
            }
        });

        setVisible(true);
    }

    void checkLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // temporary hardcoded users
        if (username.equals("admin1") && password.equals("admin123")) {
            messageLabel.setText("Login successful (admin)");
        } else if (username.equals("inst1") && password.equals("inst123")) {
            messageLabel.setText("Login successful (instructor)");
        } else if (username.equals("stu1") && password.equals("stu123")) {
            messageLabel.setText("Login successful (student)");
        } else {
            messageLabel.setText("Invalid username or password");
        }
    }

    public static void main(String[] args) {
        new LoginApp();
    }
}
