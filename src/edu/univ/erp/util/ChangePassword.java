package edu.univ.erp.util;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import edu.univ.erp.auth.Encryption;
import edu.univ.erp.ui.LoginApp;
import edu.univ.erp.data.DBConnection;

public class ChangePassword extends JFrame {

    JTextField usernameField;
    JPasswordField oldPassField;
    JPasswordField newPassField;
    JPasswordField confirmPassField;
    JButton updateBtn, backBtn;

    public ChangePassword() {
        setTitle("Change Password");
        setSize(420, 450);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Update Your Password");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        main.add(title);
        main.add(Box.createVerticalStrut(20));

        usernameField = fieldWithLabel(main, "Username");
        oldPassField = passWithLabel(main, "Old Password");
        newPassField = passWithLabel(main, "New Password");
        confirmPassField = passWithLabel(main, "Confirm Password");

        updateBtn = new JButton("Update Password");
        updateBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateBtn.setBackground(new Color(46, 110, 92));
        updateBtn.setForeground(Color.WHITE);

        backBtn = new JButton("Back to Login");
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        main.add(Box.createVerticalStrut(20));
        main.add(updateBtn);
        main.add(Box.createVerticalStrut(15));
        main.add(backBtn);

        add(main);
        setVisible(true);

        updateBtn.addActionListener(e -> changePassword());
        backBtn.addActionListener(e -> {
            dispose();
            new LoginApp();
        });
    }

    private JTextField fieldWithLabel(JPanel p, String label) {
        JLabel l = new JLabel(label);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField f = new JTextField();
        f.setMaximumSize(new Dimension(300, 35));

        p.add(l);
        p.add(f);
        p.add(Box.createVerticalStrut(10));
        return f;
    }

    private JPasswordField passWithLabel(JPanel p, String label) {
        JLabel l = new JLabel(label);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPasswordField f = new JPasswordField();
        f.setMaximumSize(new Dimension(300, 35));

        p.add(l);
        p.add(f);
        p.add(Box.createVerticalStrut(10));
        return f;
    }


    private void changePassword() {
        try {
            String username = usernameField.getText().trim();
            String oldPass = new String(oldPassField.getPassword());
            String newPass = new String(newPassField.getPassword());
            String confirmPass = new String(confirmPassField.getPassword());

            if (username.isEmpty() || oldPass.isEmpty() || newPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required");
                return;
            }

            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match");
                return;
            }

            Connection conn = DBConnection.getAuthConnection();
            Encryption enc = new Encryption();

            String sql = "SELECT password FROM users WHERE username=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "User not found");
                conn.close();
                return;
            }

            String storedHash = rs.getString("password");

            if (!enc.matches(oldPass, storedHash)) {
                JOptionPane.showMessageDialog(this, "Old password is incorrect");
                conn.close();
                return;
            }

            String newHash = enc.encrypt(newPass);
            String updateSql = "UPDATE users SET password=? WHERE username=?";
            PreparedStatement updatePS = conn.prepareStatement(updateSql);
            updatePS.setString(1, newHash);
            updatePS.setString(2, username);

            updatePS.executeUpdate();
            conn.close();

            JOptionPane.showMessageDialog(this, "Password updated successfully");
            dispose();
            new LoginApp();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
