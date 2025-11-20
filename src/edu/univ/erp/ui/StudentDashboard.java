package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;

import edu.univ.erp.access.StudentCommands;

/**
 * Student dashboard: sidebar + CardLayout, integrated JTable panels, logout button.
 */
public class StudentDashboard extends JFrame {
    private CardLayout cards = new CardLayout();
    private JPanel cardPanel;
    private StudentCommands sc = new StudentCommands();
    private int studentId;

    // central table used by several panels
    private JTable centerTable = new JTable();
    private JScrollPane centerScroll = new JScrollPane(centerTable);

    public StudentDashboard(String username) {
        this.studentId = getStudentIdByUsername(username);

        setTitle("Student Dashboard - University ERP");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Sidebar
        JPanel sidebar = new JPanel(new GridLayout(0, 1, 10, 10));
        sidebar.setBackground(new Color(31, 67, 59));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 12, 20, 12));

        String[] items = {"Home", "Catalog", "Enroll", "My Timetable", "My Grades", "Profile"};
        for (String it : items) {
            JButton b = new JButton(it);
            styleSidebarButton(b);
            b.addActionListener(e -> cards.show(cardPanel, it));
            sidebar.add(b);
        }

        // Card area
        cardPanel = new JPanel(cards);
        cardPanel.add(makeTopPanel("Welcome, Student"), "Home");
        cardPanel.add(catalogPanel(), "Catalog");
        cardPanel.add(enrollPanel(), "Enroll");
        cardPanel.add(timetablePanel(), "My Timetable");
        cardPanel.add(gradesPanel(), "My Grades");
        cardPanel.add(profilePanel(), "Profile");

        add(sidebar, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);

        cards.show(cardPanel, "Home");
        setVisible(true);
    }

    private void styleSidebarButton(JButton b) {
        b.setBackground(new Color(60, 135, 110));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false);
    }

    // top area with title + logout button
    private JPanel makeTopPanel(String titleText) {
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        top.setBackground(new Color(245, 247, 246));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginApp());
        });

        // right side: logout
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(logout);

        top.add(title, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        // Wrap into a main panel that also contains the table area for consistency
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(top, BorderLayout.NORTH);

        // reset central table
        centerTable.setModel(new javax.swing.table.DefaultTableModel());
        centerScroll.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));
        wrapper.add(centerScroll, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel catalogPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(makeTopPanel("Course Catalog"), BorderLayout.NORTH);

        JPanel north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton load = new JButton("Load Catalog");
        north.add(load);
        p.add(north, BorderLayout.NORTH);

        load.addActionListener(e -> {
            try {
                ResultSet rs = sc.browseCatalog();
                if (rs != null) {
                    centerTable.setModel(TableUtils.resultSetToTableModel(rs));
                } else {
                    centerTable.setModel(new javax.swing.table.DefaultTableModel());
                    JOptionPane.showMessageDialog(this, "No courses available.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading catalog: " + ex.getMessage());
            }
        });

        return p;
    }

    private JPanel enrollPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(makeTopPanel("Enroll in a Section"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 120, 20, 120));
        JTextField secId = new JTextField();
        form.add(new JLabel("Section ID to Enroll:"));
        form.add(secId);

        JButton enroll = new JButton("Enroll");
        form.add(new JLabel());
        form.add(enroll);

        enroll.addActionListener(e -> {
            try {
                boolean ok = sc.registerForSection(studentId, Integer.parseInt(secId.getText().trim()));
                JOptionPane.showMessageDialog(this, ok ? "Enrolled successfully." : "Failed to enroll (maybe full or duplicate).");
                // optionally refresh timetable or catalog if desired
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid section id.");
            }
        });

        p.add(form, BorderLayout.CENTER);
        return p;
    }

    private JPanel timetablePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(makeTopPanel("My Timetable"), BorderLayout.NORTH);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton load = new JButton("Load Timetable");
        top.add(load);
        p.add(top, BorderLayout.NORTH);

        load.addActionListener(e -> {
            try {
                ResultSet rs = sc.viewTimetable(studentId);
                if (rs != null) centerTable.setModel(TableUtils.resultSetToTableModel(rs));
                else {
                    centerTable.setModel(new javax.swing.table.DefaultTableModel());
                    JOptionPane.showMessageDialog(this, "No enrollments found.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading timetable: " + ex.getMessage());
            }
        });

        return p;
    }

    private JPanel gradesPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(makeTopPanel("My Grades"), BorderLayout.NORTH);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton load = new JButton("Load Grades");
        top.add(load);
        p.add(top, BorderLayout.NORTH);

        load.addActionListener(e -> {
            try {
                ResultSet rs = sc.viewGrades(studentId);
                if (rs != null) centerTable.setModel(TableUtils.resultSetToTableModel(rs));
                else {
                    centerTable.setModel(new javax.swing.table.DefaultTableModel());
                    JOptionPane.showMessageDialog(this, "No grades available.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading grades: " + ex.getMessage());
            }
        });

        return p;
    }

    private JPanel profilePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(makeTopPanel("My Profile"), BorderLayout.NORTH);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton load = new JButton("Load Profile");
        top.add(load);
        p.add(top, BorderLayout.NORTH);

        load.addActionListener(e -> {
            try {
                // if you have sc.viewProfile(studentId) returning ResultSet, use it.
                // otherwise reuse timetable as a fallback
                ResultSet rs = sc.viewTimetable(studentId); // change to profile query if exists
                if (rs != null) {
                    centerTable = TableUtils.buildStyledTable(rs);
                    centerScroll.setViewportView(centerTable);
                }
                else centerTable.setModel(new javax.swing.table.DefaultTableModel());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading profile: " + ex.getMessage());
            }
        });

        return p;
    }

    // keep your original resolver logic
    private int getStudentIdByUsername(String username) {
    String sql =
        "SELECT s.user_id FROM students s " +
        "JOIN auth_db.users u ON s.user_id = u.id " +
        "WHERE u.username = ?";

    try (java.sql.Connection conn = edu.univ.erp.data.DBConnection.getErpConnection();
         java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, username);
        java.sql.ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("user_id");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return -1; // not found
}

}
