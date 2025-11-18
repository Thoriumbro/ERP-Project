package edu.univ.erp.ui;

import javax.swing.*;

import edu.univ.erp.auth.*;
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

    public LoginApp() {

        setTitle("Login");
        setSize(380, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(230, 240, 236));

        // ---------------- TOP ----------------
        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(380, 230));
        topPanel.setBackground(Color.WHITE);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        topPanel.add(Box.createVerticalStrut(25));

        JLabel logo = new JLabel("\u263A", SwingConstants.CENTER);
        logo.setFont(new Font("Serif", Font.BOLD, 60));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(logo);

        topPanel.add(Box.createVerticalStrut(10));

        JLabel company = new JLabel("Welcome to ERP");
        company.setAlignmentX(Component.CENTER_ALIGNMENT);
        company.setFont(new Font("SansSerif", Font.BOLD, 16));
        company.setForeground(new Color(30, 80, 60));
        topPanel.add(company);

        topPanel.add(Box.createVerticalStrut(20));

        // ---------------- CENTER ----------------
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(Box.createVerticalStrut(30));

        JLabel loginText = new JLabel("Login");
        loginText.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginText.setFont(new Font("SansSerif", Font.BOLD, 26));
        loginText.setForeground(new Color(40, 70, 60));
        centerPanel.add(loginText);

        JLabel subText = new JLabel("Sign in to continue");
        subText.setAlignmentX(Component.CENTER_ALIGNMENT);
        subText.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subText.setForeground(new Color(70, 100, 90));
        centerPanel.add(subText);

        centerPanel.add(Box.createVerticalStrut(25));

        // ---------------- USERNAME ----------------
        JLabel nameLabel = new JLabel("Name");
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        nameLabel.setForeground(new Color(40, 70, 60));
        centerPanel.add(nameLabel);

        centerPanel.add(Box.createVerticalStrut(5));

        usernameField = new JTextField();
        styleTextField(usernameField);
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(usernameField);

        centerPanel.add(Box.createVerticalStrut(15));

        // ---------------- PASSWORD ----------------
        JLabel passLabel = new JLabel("Password");
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passLabel.setForeground(new Color(40, 70, 60));
        centerPanel.add(passLabel);

        centerPanel.add(Box.createVerticalStrut(5));

        passwordField = new JPasswordField();
        styleTextField(passwordField);
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(passwordField);

        centerPanel.add(Box.createVerticalStrut(20));

        // ---------------- LOGIN BUTTON ----------------
        loginButton = new JButton("Log In");
        styleButton(loginButton);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(loginButton);

        // ---------------- Bottom ----------------
        centerPanel.add(Box.createVerticalStrut(25));

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);

        // ---------------- ACTION LISTENER ----------------
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {
                    Connection conn = DBConnection.getAuthConnection();

                    String username = usernameField.getText();
                    String typedPassword = new String(passwordField.getPassword());

                    String sql = "SELECT password, role, last_login FROM users WHERE username=?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, username);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {

                        String storedHash = rs.getString("password");
                        String role = rs.getString("role");
                        String lastLogin = rs.getString("last_login");

                        Encryption enc = new Encryption();
                        LoginService loginService = new LoginService();

                        if (enc.matches(typedPassword, storedHash)) {
                            loginService.updateLastLogin(username, conn);
                            loginService.showLastLoginMessage(role, lastLogin);

                            dispose();

                            if (role.equalsIgnoreCase("admin")) {
                                new AdminDashboard();
                            } else if (role.equalsIgnoreCase("instructor")) {
                                new InstructorDashboard(username);
                            } else {
                                new StudentDashboard(username);
                            }

                        } else {
                            JOptionPane.showMessageDialog(null, "Invalid username or password");
                        }

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


    // -------- Styling helpers --------

    private void styleTextField(JTextField field) {
        field.setMaximumSize(new Dimension(260, 40));
        field.setBackground(new Color(160, 200, 180));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    private void styleButton(JButton btn) {
        btn.setMaximumSize(new Dimension(260, 40));
        btn.setBackground(new Color(40, 90, 70));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }


    public static void main(String[] args) {
        new LoginApp();
    }
}
